/**
 * MXFTechMDExtractor.java
 * Authors: Francesco Gallo (gallo@eurix.it), Laurent Boch (l.boch@rai.it), Roberto Borgotallo (r.borgotallo@rai.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 EURIX Srl, Torino, Italy
 * Copyright (C) 2009-2012 RAI CRIT, Torino, Italy
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.prestoprime.plugin.p4.tools;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.tools.GenericTool;
import eu.prestoprime.tools.ToolException;

public class MXFTechMDExtractor implements GenericTool {

	private Logger logger = LoggerFactory.getLogger(MXFTechMDExtractor.class);

	private Map<String, String> attributeMap;
	private Map<String, String> propertyMap;
	private List<String> attributeNames;
	private static final short SMPTEKEYSIZE = 16;

	public MXFTechMDExtractor() {
		init();
	}

	public String getAttributeByName(String name) {
		return attributeMap.get(name);
	}

	public List<String> getSupportedAttributeNames() {
		return attributeNames;
	}

	public void extract(String mxfFile) throws ToolException {

		try {

			RandomAccessFile mxfin = new RandomAccessFile(mxfFile, "r");

			MXFBuffer mxfbuftmp = new MXFBuffer(2048);
			mxfbuftmp.loadBuffer(mxfin);
			// loaded buffer
			long endofheaderpos = getAllFromPartitionPack(mxfbuftmp);
			// OperationPattern and EssenceContain into the Map
			mxfin.seek(0);
			MXFBuffer mxfbuf = new MXFBuffer((int) endofheaderpos);
			mxfbuf.loadBuffer(mxfin);

			getMaterialPackageId(mxfbuf);
			getAllFromPictureEssenceDescriptor(mxfbuf);
			getAllFromSoundEssenceDescriptor(mxfbuf);
			getEditRate(mxfbuf);
			getDuration(mxfbuf);

			setPictureDefaultValues();
			setVideoActiveLinesPerFrame();
			setFrameLayoutName();

		} catch (Exception e) {
			throw new ToolException("Error extracting MD from MXF file: " + mxfFile);
		}

	}

	private void init() {

		// Load list of MXF properties
		initPropertyMap();

		// Populate attribute map with empty values
		initAttributeMap();
	}

	private void initPropertyMap() {

		propertyMap = new LinkedHashMap<String, String>();

		// Material Package Identifier (Type UMID)
		propertyMap.put("materialpackage.id.name", "MaterialPackageID");
		propertyMap.put("materialpackage.id.smptekey", "06 0e 2b 34 02 53 01 01 0d 01 01 01 01 01 36 00");
		propertyMap.put("umid.localtag", "44 01 00 20");
		// Partition Pack
		// antepenult byte: 02 Header, 03 Body, 04 Footer
		// penultimate byte: 01 Open and Incomplete, 02 Closed and incomplete,
		// 03 Open and Complete, 04 Closed and Complete
		propertyMap.put("partition.pack.header.name", "PartitionPack");
		propertyMap.put("partition.pack.key", "06 0e 2b 34 02 05 01 01 0d 01 02 01 01 xx xx 00");
		propertyMap.put("partition.pack.smptekey", "06 0e 2b 34 02 05 01 01 0d 01 02 01 01 ff ff 00");
		// OperationalPattern
		// fourth- byte Item Complexity
		// example for op1A 06 0e 2b 34 04 01 01 01 0d 01 02 01 01 01 09 00
		propertyMap.put("operational.pattern.name", "OperationalPattern");
		propertyMap.put("operational.pattern.smptekey", "06 0e 2b 34 04 01 01 01 0d 01 02 01 ff ff ff ff");
		// EssenceContainers
		/*
		 * D10 see Mapping S386
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 01 01 01" D10 50Mbit 625
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 01 02 01" D10 50Mbit 525
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 01 03 01" D10 40Mbit 625
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 01 04 01" D10 40Mbit 525
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 01 05 01" D10 30Mbit 625
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 01 06 01" D10 30Mbit 525
		 * 
		 * MPEG see Mapping S381
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 04 xx xx" MPEG ES
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 07 xx xx" MPEG PES
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 08 xx xx" MPEG PS
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 09 xx xx" MPEG TS
		 * 
		 * DV see Mapping S381 -->
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 02 ff ff"
		 * 
		 * Uncompressed see Mapping S384 -->
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 05 ff ff"
		 * 
		 * D11 see Mapping S387 -->
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 03 ff ff"
		 * 
		 * AES see Mapping S382 -->
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 06 01 00" AES-BWF Wave
		 * Framewrapped "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 06 02 00"
		 * AES-BWF Wave Clip Wrapped
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 06 03 00" AES-BWF AES Frame
		 * Wrapped "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 06 04 00" AES-BWF AES
		 * Clip Wrapped "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 06 08 00"
		 * AES-BWF Wave Custom Wrapped
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 06 09 00" AES-BWF AES Custom
		 * Wrapped
		 * 
		 * JPEG2000 see Mapping S422
		 * "06 0e 2b 34 04 01 01 01 0d 01 03 01 02 0c ff ff"
		 */
		propertyMap.put("essence.container.name", "EssenceContainers");
		propertyMap.put("essence.container.smptekey", "06 0e 2b 34 04 01 01 1ff 0d 01 03 01 02 1ff 1ff 1ff");
		// Timeline Track
		propertyMap.put("timeline.track.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 3b 00");
		propertyMap.put("editrate.smptekey", "06 0e 2b 34 01 01 01 02 05 30 04 05 00 00 00 00");
		propertyMap.put("editrate.localtag", "4b 01 00 08");
		propertyMap.put("editrate.name", "EditRate");
		// Durations
		propertyMap.put("structural.component.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 02 00");
		propertyMap.put("essence.track.smptekey", "06 0e 2b 34 04 01 01 01 01 03 02 02 101 00 00 00");
		propertyMap.put("duration.smptekey", "06 0e 2b 34 01 01 01 02 07 02 02 01 01 03 00 00");
		propertyMap.put("duration.localtag", "02 02 00 08");
		propertyMap.put("duration.name", "Duration");
		// Picture EssenceDescriptor
		propertyMap.put("generic.picture.essence.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 27 00");
		propertyMap.put("cdci.essence.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 28 00");
		propertyMap.put("rgba.essence.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 29 00");
		propertyMap.put("mpeg.essence.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 51 00");
		// Generic Sound EssenceDescriptor
		propertyMap.put("generic.sound.essence.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 42 00");
		propertyMap.put("wave.audio.essence.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 48 00");
		propertyMap.put("aes3.audio.essence.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 47 00");
		propertyMap.put("wave.audio.physical.descriptor.smptekey", "06 0e 2b 34 02 53 01 101 0d 01 01 01 01 01 50 00");
		// Other EssenceDescriptors to be verified
		propertyMap.put("jpeg200.essence.descriptor.smptekey", "06 0e 2b 34 02 05 01 101 0d 01 02 01 01 01 5a 00");
		// ComponentDepth
		propertyMap.put("componentdepth.name", "ComponentDepth");
		propertyMap.put("componentdepth.smptekey", "06 0e 2b 34 01 01 01 02 04 01 05 03 0a 00 00 00");
		propertyMap.put("componentdepth.localtag", "33 01 00 04");
		// HorizontalSubSampling
		propertyMap.put("horizontalsubsampling.name", "HorizontalSubSampling");
		propertyMap.put("horizontalsubsampling.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 05 00 00 00");
		propertyMap.put("horizontalsubsampling.localtag", "33 02 00 04");
		// VerticalSubSampling
		propertyMap.put("verticalsubsampling.name", "VerticalSubSampling");
		propertyMap.put("verticalsubsampling.smptekey", "06 0e 2b 34 01 01 01 02 04 01 05 01 10 00 00 00");
		propertyMap.put("verticalsubsampling.localtag", "33 08 00 04");
		// PictureEssenceCoding
		propertyMap.put("picture.essencecoding.name", "PictureEssenceCoding");
		propertyMap.put("picture.essencecoding.smptekey", "06 0e 2b 34 01 01 01 02 04 01 03 01 06 00 00 00");
		propertyMap.put("picture.essencecoding.localtag", "32 01 00 10");
		// ActiveFormatDescriptor
		// SMPTE 2016-1 (4 bit encoding)
		propertyMap.put("activeformatdescriptor.name", "ActiveFormatDescriptor");
		propertyMap.put("activeformatdescriptor.smptekey", "06 0e 2b 34 01 01 01 05 04 01 03 02 09 00 00 00");
		propertyMap.put("activeformatdescriptor.localtag", "32 18 00 01");
		// Generic Picture Essence Descriptors
		// FrameLayout
		propertyMap.put("framelayout.name", "FrameLayout");
		propertyMap.put("framelayout.smptekey", "06 0e 2b 34 01 01 01 01 04 01 03 01 04 00 00 00");
		propertyMap.put("framelayout.localtag", "32 0c 00 01");
		propertyMap.put("framelayoutname.name", "FrameLayoutName");
		// StoredWidth
		propertyMap.put("stored.width.name", "StoredWidth");
		propertyMap.put("stored.width.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 02 02 00 00 00");
		propertyMap.put("stored.width.localtag", "32 03 00 04");
		// StoredHeight
		propertyMap.put("stored.height.name", "StoredHeight");
		propertyMap.put("stored.height.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 02 01 00 00 00");
		propertyMap.put("stored.height.localtag", "32 02 00 04");
		// SampleWidth
		propertyMap.put("sampled.width.name", "SampledWidth");
		propertyMap.put("sampled.width.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 08 00 00 00");
		propertyMap.put("sampled.width.localtag", "32 05 00 04");
		// SampleHeight
		propertyMap.put("sampled.height.name", "SampledHeight");
		propertyMap.put("sampled.height.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 07 00 00 00");
		propertyMap.put("sampled.height.localtag", "32 04 00 04");
		// SampleXOffset
		propertyMap.put("sampled.xoffset.name", "SampledXOffset");
		propertyMap.put("sampled.xoffset.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 09 00 00 00");
		propertyMap.put("sampled.xoffset.localtag", "32 06 00 04");
		// SampleYOffset
		propertyMap.put("sampled.yoffset.name", "SampledYOffset");
		propertyMap.put("sampled.yoffset.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 0a 00 00 00");
		propertyMap.put("sampled.yoffset.localtag", "32 07 00 04");
		// DisplayHeight
		propertyMap.put("display.height.name", "DisplayHeight");
		propertyMap.put("display.height.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 0b 00 00 00");
		propertyMap.put("display.height.localtag", "32 08 00 04");
		// DisplayWidth
		propertyMap.put("display.width.name", "DisplayWidth");
		propertyMap.put("display.width.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 0c 00 00 00");
		propertyMap.put("display.width.localtag", "32 09 00 04");
		// DisplayXOffset
		propertyMap.put("display.xoffset.name", "DisplayXOffset");
		propertyMap.put("display.xoffset.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 0d 00 00 00");
		propertyMap.put("display.xoffset.localtag", "32 0a 00 04");
		// DisplayYOffset
		propertyMap.put("display.yoffset.name", "DisplayYOffset");
		propertyMap.put("display.yoffset.smptekey", "06 0e 2b 34 01 01 01 01 04 01 05 01 0e 00 00 00");
		propertyMap.put("display.yoffset.localtag", "32 0b 00 04");
		// AspectRatio
		propertyMap.put("aspectratio.name", "AspectRatio");
		propertyMap.put("aspectratio.smptekey", "06 0e 2b 34 01 01 01 01 04 01 01 01 01 00 00 00");
		propertyMap.put("aspectratio.localtag", "32 0e 00 08");
		// Generic Sound Essence Descriptor
		// AudioSamplingRate
		propertyMap.put("audio.samplingrate.name", "AudioSamplingRate");
		propertyMap.put("audio.samplingrate.smptekey", "06 0e 2b 34 01 01 01 05 04 02 03 01 01 01 00 00");
		propertyMap.put("audio.samplingrate.localtag", "3d 03 00 08");
		// ChannelCount
		propertyMap.put("audio.channelcount.name", "AudioChannelCount");
		propertyMap.put("audio.channelcount.smptekey", "06 0e 2b 34 01 01 01 05 04 02 01 01 04 00 00 00");
		propertyMap.put("audio.channelcount.localtag", "3d 07 00 04");
		// QuantizationBits
		propertyMap.put("audio.quantizationbits.name", "AudioQuantizationBits");
		propertyMap.put("audio.quantizationbits.smptekey", "06 0e 2b 34 01 01 01 04 04 02 03 03 04 00 00 00");
		propertyMap.put("audio.quantizationbits.localtag", "3d 01 00 04");
		// SoundEssenceCoding
		propertyMap.put("audio.soundessencecoding.name", "SoundEssenceCoding");
		propertyMap.put("audio.soundessencecoding.smptekey", "06 0e 2b 34 01 01 01 02 04 02 04 02 00 00 00 00");
		propertyMap.put("audio.soundessencecoding.localtag", "3d 01 00 16");
		// VideoActiveLinesPerFrame
		propertyMap.put("videoactivelinesperframe.name", "VideoActiveLinesPerFrame");

	}

	private void initAttributeMap() {

		// List of attribute names
		attributeNames = new ArrayList<String>();
		// Hash Table with Attribute Names and Values
		attributeMap = new HashMap<String, String>();
		Set<String> attrSet = propertyMap.keySet();
		Iterator<String> attriIter = attrSet.iterator();
		while (attriIter.hasNext()) {
			String attrKey = attriIter.next();
			if (attrKey.contains(".name")) {
				attributeNames.add(propertyMap.get(attrKey));
				attributeMap.put(attrKey, null);
			}
		}

	}

	private long getAllFromPartitionPack(MXFBuffer mxfbuf) throws ToolException {
		long EndOfHeaderPos;
		int[] partkey = new int[SMPTEKEYSIZE];
		partkey = null;
		// get PartitionPackKey from properties
		partkey = initsmptekey(propertyMap.get("partition.pack.smptekey"));
		if (partkey == null)
			throw new ToolException("partition.pack.key not found");

		if (mxfbuf.checksmptekey(0, partkey, 13)) {
			int partitionkind = mxfbuf.buf[13];
			int partitionstatus = mxfbuf.buf[14];
			String partitionPack = "";
			switch (partitionkind) {
			case 2:
				partitionPack += "Header Partition: ";
				break;
			case 3:
				partitionPack += "Body Partition: ";
				break;
			case 4:
				partitionPack += "Footer Partition: ";
				break;
			default:
				throw new ToolException("parsePartitionPack: Unknown Partition kind (Byte[13] value:" + partitionkind + ")");
			}
			switch (partitionstatus) {
			case 1:
				partitionPack += "Open and Incomplete";
				break;
			case 2:
				partitionPack += "Closed and Incomplete";
				break;
			case 3:
				partitionPack += "Open and Complete";
				break;
			case 4:
				partitionPack += "Closed and Complete";
				break;
			default:
				throw new ToolException("parsePartitionPack: Unknown Partition Status (Byte[14] value:" + partitionstatus + ")");
			}
			attributeMap.put("PartitionPack", partitionPack);
		} else {
			throw new ToolException("Partition Pack NOT FOUND. Not an MXF file???");
		}

		int pos = 16;
		long packlen = mxfbuf.parseBERlength(pos);
		pos += mxfbuf.BERlengthOffset(pos);
		pos += 2;
		pos += 2;
		long KagSize = mxfbuf.getUint(pos, 4);
		EndOfHeaderPos = (packlen / KagSize) * KagSize + KagSize;
		pos += 4;
		pos += 8;
		pos += 8;
		pos += 8;
		long HeaderByteCount = mxfbuf.getInt(pos, 8);
		EndOfHeaderPos += HeaderByteCount;
		pos += 8;
		pos += 8;
		pos += 4;
		pos += 8;
		pos += 4;

		int[] opkey = initsmptekey(propertyMap.get("operational.pattern.smptekey"));
		if (mxfbuf.checksmptekey(pos, opkey, 12)) {
			pos += 12;
			int ItemComplexity = mxfbuf.buf[pos++];
			int PackageComplexity = mxfbuf.buf[pos++];
			String operationalpattern = "OP" + ItemComplexity;

			switch (PackageComplexity) {
			case 1:
				operationalpattern += "a";
				break;
			case 2:
				operationalpattern += "b";
				break;
			case 3:
				operationalpattern += "c";
				break;
			}
			attributeMap.put("OperationalPattern", operationalpattern);
			logger.debug("Operational Pattern: " + operationalpattern);
		} else {
			throw new ToolException("Operational Pattern NOT FOUND. Not an MXF file???");
		}
		pos += 2;
		// find essence container
		pos += 8; // EC batch label (8B)
		long numEC = (packlen + 16 + 8 + mxfbuf.BERlengthOffset(16) - pos) / 16;
		int[] ECmapkey = initsmptekey(propertyMap.get("essence.container.smptekey"));
		String EssenceContainer = "";
		String EssenceContainers = "";
		for (int i = 0; i < numEC; i++) {
			if (mxfbuf.checksmptekey(pos, ECmapkey, 13)) {
				// logger.debug("parsePartitionPack: found Essence Container UL "+mxfbuf.buf[pos+13]);
				switch (mxfbuf.buf[pos + 13]) {
				case 1:
					EssenceContainer = "D10 Mapping";
					break;
				case 2:
					EssenceContainer = "DV Mapping";
					break;
				case 3:
					EssenceContainer = "D11 Mapping";
					break;
				case 4:
					EssenceContainer = "MPEG ES Mapping";
					break;
				case 5:
					EssenceContainer = "Uncompressed Picture Mapping";
					break;
				case 6:
					EssenceContainer = "AES-BWF Mapping";
					break;
				case 7:
					EssenceContainer = "MPEG PES Mapping";
					break;
				case 8:
					EssenceContainer = "MPEG PS Mapping";
					break;
				case 9:
					EssenceContainer = "MPEG TS Mapping";
					break;
				case 12: // 0x0c
					EssenceContainer = "JPEG 2000 Picture Element Mapping";
					break;
				case 127:
					EssenceContainer = "Generic Essence Multiple Mappings";
					break;
				default:
					EssenceContainer = "Unknown Mapping (" + mxfbuf.buf[pos + 14] + ")" + mxfbuf.getUL(pos, 16);
					break;
				}
				EssenceContainers = EssenceContainers + EssenceContainer + ",";
				logger.debug("EssenceContainer: " + EssenceContainer);
			} else {
				logger.debug("Essence Container not found! " + mxfbuf.getUL(pos, 16));
			}
			pos += 16;
		}
		EssenceContainers = EssenceContainers.substring(0, EssenceContainers.length() - 1);
		attributeMap.put("EssenceContainers", EssenceContainers);
		return (EndOfHeaderPos);
	}

	private void getMaterialPackageId(MXFBuffer mxfbuf) throws Exception {
		// find MAterial Package ID
		int[] key = null;
		int numfound = 0;
		int pos = 0;
		int prevpos = pos;
		long packlen = 0;
		key = initsmptekey(propertyMap.get("materialpackage.id.smptekey"));
		boolean done = false;
		while (!done) {
			prevpos = pos;
			pos = mxfbuf.findsmptekey(prevpos, key, mxfbuf.bufreallen - 16, 16);
			if (pos == -1) {
				// logger.debug("Found "+ numfound
				// +" materialpackage.id.smptekey");
				done = true;
			} else {
				pos += 16;
				packlen = mxfbuf.parseBERlength(pos);
				getLocalMDField(mxfbuf, pos, (int) packlen, "umid.localtag", "MaterialPackageID", 4, 32, true);
				pos += (int) packlen;
				numfound++;
			}
		}
	}

	private void getAllFromPictureEssenceDescriptor(MXFBuffer mxfbuf) throws Exception {
		int[] key = initsmptekey(propertyMap.get("generic.picture.essence.descriptor.smptekey"));
		int pos = 0;
		pos = mxfbuf.findsmptekey(pos, key, mxfbuf.bufreallen - 16, 16);
		if (pos == -1) {
			key = initsmptekey(propertyMap.get("cdci.essence.descriptor.smptekey"));
			pos = mxfbuf.findsmptekey(0, key, mxfbuf.bufreallen - 16, 16);
			if (pos == -1) {
				key = initsmptekey(propertyMap.get("mpeg.essence.descriptor.smptekey"));
				pos = mxfbuf.findsmptekey(0, key, mxfbuf.bufreallen - 16, 16);
			}
			if (pos == -1) {
				key = initsmptekey(propertyMap.get("rgba.essence.descriptor.smptekey"));
				pos = mxfbuf.findsmptekey(0, key, mxfbuf.bufreallen - 16, 16);
			}
		}

		if (pos == -1) {
			logger.debug("No picture essence descriptor found");
		} else {
			pos += 16;
			long cdcipacklen = mxfbuf.parseBERlength(pos);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "display.width.localtag", "DisplayWidth", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "display.height.localtag", "DisplayHeight", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "sampled.width.localtag", "SampledWidth", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "sampled.height.localtag", "SampledHeight", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "stored.width.localtag", "StoredWidth", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "stored.height.localtag", "StoredHeight", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "stored.height.localtag", "StoredHeight", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "framelayout.localtag", "FrameLayout", 1, 1);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "activeformatdescriptor.localtag", "ActiveFormatDescriptor", 1, 1);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "componentdepth.localtag", "ComponentDepth", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "horizontalsubsampling.localtag", "HorizontalSubSampling", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "verticalsubsampling.localtag", "VerticalSubSampling", 1, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "aspectratio.localtag", "AspectRatio", 3, 8);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "picture.essencecoding.localtag", "PictureEssenceCoding", 4, 16);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "display.xoffset.localtag", "DisplayXOffset", 2, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "display.yoffset.localtag", "DisplayYOffset", 2, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "sampled.xoffset.localtag", "SampledXOffset", 2, 4);
			getLocalMDField(mxfbuf, pos, (int) cdcipacklen, "sampled.yoffset.localtag", "SampledYOffset", 2, 4);
		}

	}

	private void getAllFromSoundEssenceDescriptor(MXFBuffer mxfbuf) throws Exception {
		int[] key = null;
		int pos = 0;
		int prevpos = pos;
		boolean done = false;
		long packlen = 0;
		while (!done) {
			prevpos = pos;
			key = initsmptekey(propertyMap.get("generic.sound.essence.descriptor.smptekey"));
			pos = mxfbuf.findsmptekey(pos, key, mxfbuf.bufreallen - 16, 16);
			if (pos == -1) {
				key = initsmptekey(propertyMap.get("aes3.audio.essence.descriptor.smptekey"));
				pos = mxfbuf.findsmptekey(prevpos, key, mxfbuf.bufreallen - 16, 16);
				if (pos == -1) {
					key = initsmptekey(propertyMap.get("wave.audio.essence.descriptor.smptekey"));
					pos = mxfbuf.findsmptekey(prevpos, key, mxfbuf.bufreallen - 16, 16);
				}
			}
			if (pos == -1) {
				done = true;
				logger.debug("No generic sound or wave audio essence descriptor found");
			} else {
				pos += 16;
				packlen = mxfbuf.parseBERlength(pos);
				getLocalMDField(mxfbuf, pos, (int) packlen, "audio.samplingrate.localtag", "AudioSamplingRate", 3, 8);
				getLocalMDField(mxfbuf, pos, (int) packlen, "audio.quantizationbits.localtag", "AudioQuantizationBits", 1, 4);
				getLocalMDField(mxfbuf, pos, (int) packlen, "audio.soundessencecoding.localtag", "SoundEssenceCoding", 4, 16);

				int AudioChannelCount = 0;
				if (attributeMap.get("AudioChannelCount") != null) {
					AudioChannelCount = Integer.parseInt(attributeMap.get("AudioChannelCount"));
					attributeMap.put("AudioChannelCount", null);
				}
				getLocalMDField(mxfbuf, pos, (int) packlen, "audio.channelcount.localtag", "AudioChannelCount", 1, 4);
				if (attributeMap.get("AudioChannelCount") != null) {
					AudioChannelCount += Integer.parseInt(attributeMap.get("AudioChannelCount"));
				}
				attributeMap.put("AudioChannelCount", Integer.toString(AudioChannelCount));
			}
			pos += (int) packlen;
		}
	}

	private void getEditRate(MXFBuffer mxfbuf) throws Exception {
		int[] key = null;
		int numfound = 0;
		int pos = 0;
		int prevpos = pos;
		long packlen = 0;
		key = initsmptekey((String) propertyMap.get("timeline.track.smptekey"));

		// TODO find all
		boolean done = false;
		while (!done) {
			prevpos = pos;
			pos = mxfbuf.findsmptekey(prevpos, key, mxfbuf.bufreallen - 16, 16);
			if (pos == -1) {
				done = true;
			} else {
				pos += 16;
				packlen = mxfbuf.parseBERlength(pos);
				getLocalMDField(mxfbuf, pos, (int) packlen, "editrate.localtag", "EditRate", 3, 8);
				pos += (int) packlen;
			}
		}
	}

	private void getDuration(MXFBuffer mxfbuf) throws Exception {
		int[] key = null;
		int numfound = 0;
		int pos = 0;
		int prevpos = pos;
		long packlen = 0;
		key = initsmptekey(propertyMap.get("essence.track.smptekey"));
		boolean done = false;
		while (!done) {
			prevpos = pos;
			pos = mxfbuf.findsmptekey(prevpos, key, mxfbuf.bufreallen - 16, 16);
			if (pos == -1) {
				done = true;
			} else {
				pos += 16;
				packlen = mxfbuf.parseBERlength(pos);
				getLocalMDField(mxfbuf, pos, (int) packlen, "duration.localtag", "Duration", 1, 8);
				pos += (int) packlen;
			}
		}
	}

	private void setPictureDefaultValues() throws Exception {
		if (attributeMap.get("StoredWidth") != null && attributeMap.get("SampledWidth") == null) {
			attributeMap.put("SampledWidth", attributeMap.get("StoredWidth"));
		}
		if (attributeMap.get("StoredHeight") != null && attributeMap.get("SampledHeight") == null) {
			attributeMap.put("SampledHeight", attributeMap.get("StoredHeight"));
		}
		if (attributeMap.get("SampledWidth") != null && attributeMap.get("DisplayWidth") == null) {
			attributeMap.put("DisplayWidth", attributeMap.get("SampledWidth"));
		}
		if (attributeMap.get("SampledHeight") != null && attributeMap.get("DisplayHeight") == null) {
			attributeMap.put("DisplayHeight", attributeMap.get("SampledHeight"));
		}
		if (attributeMap.get("StoredWidth") != null && attributeMap.get("SampledXOffset") == null) {
			attributeMap.put("SampledXOffset", "0");
		}
		if (attributeMap.get("SampledWidth") != null && attributeMap.get("DisplayXOffset") == null) {
			attributeMap.put("DisplayXOffset", "0");
		}
		if (attributeMap.get("StoredHeight") != null && attributeMap.get("SampledYOffset") == null) {
			attributeMap.put("SampledYOffset", "0");
		}
		if (attributeMap.get("SampledHeight") != null && attributeMap.get("DisplayYOffset") == null) {
			attributeMap.put("DisplayYOffset", "0");
		}

	}

	private void setVideoActiveLinesPerFrame() throws Exception {
		if (attributeMap.get("DisplayHeight") != null && attributeMap.get("FrameLayout") != null) {
			int framelayout = 0;
			int numlines = 0;
			framelayout = Integer.parseInt(attributeMap.get("FrameLayout"));
			numlines = Integer.parseInt(attributeMap.get("DisplayHeight"));
			if (framelayout > 0)
				numlines *= 2;
			attributeMap.put("VideoActiveLinesPerFrame", Integer.toString(numlines));
		}
	}

	private void setFrameLayoutName() throws Exception {
		if (attributeMap.get("FrameLayout") != null) {
			try {
				int fl = Integer.parseInt(attributeMap.get("FrameLayout"));
				switch (fl) {
				case 0:
					attributeMap.put("FrameLayoutName", "FULL_FRAME");
					break;
				case 1:
					attributeMap.put("FrameLayoutName", "SEPARATE_FIELDS");
					break;
				case 2:
					attributeMap.put("FrameLayoutName", "SINGLE_FIELD");
					break;
				case 3:
					attributeMap.put("FrameLayoutName", "MIXED_FIELDS");
					break;
				case 4:
					attributeMap.put("FrameLayoutName", "SEGMENTED_FRAME");
					break;
				default:
					attributeMap.put("FrameLayoutName", "Unknown value (" + fl + ")");
					break;

				}
			} catch (Exception e) {
				throw new ToolException("SetFrameLayoutName: FrameLayout " + attributeMap.get("FrameLayout") + " was not an Integer");
			}
		}
	}

	private void getLocalMDField(MXFBuffer mxfbuf, int pos, int cdcilen, String localtag, String MapEntry, int datatype, int datalen) throws ToolException {
		getLocalMDField(mxfbuf, pos, cdcilen, localtag, MapEntry, datatype, datalen, false);
	}

	private void getLocalMDField(MXFBuffer mxfbuf, int pos, int cdcilen, String localtag, String MapEntry, int datatype, int datalen, boolean append) throws ToolException {
		int[] localkey = null;
		int locpos = pos;
		try {
			localkey = initsmptekey(propertyMap.get(localtag));
		} catch (NullPointerException e) {
			throw new ToolException("Key of local tag " + localtag + " not found. initsmptekey failed");
		}
		locpos = mxfbuf.findsmptekey(pos, localkey, cdcilen, 4); // localtag
																	// (4B)
		if (locpos > 0) {
			String oldEntry = attributeMap.get(MapEntry);
			if (append && oldEntry != null)
				oldEntry += ",";
			if (oldEntry == null)
				append = false;
			switch (datatype) {
			case 1: // Uint
				if (!append) {
					attributeMap.put(MapEntry, Long.toString(mxfbuf.getUint(locpos + 4, datalen)));
				} else {
					attributeMap.put(MapEntry, oldEntry + Long.toString(mxfbuf.getUint(locpos + 4, datalen)));
				}
				break;
			case 2: // Signed Int
				if (!append) {
					attributeMap.put(MapEntry, Long.toString(mxfbuf.getInt(locpos + 4, datalen)));
				} else {
					attributeMap.put(MapEntry, oldEntry + Long.toString(mxfbuf.getInt(locpos + 4, datalen)));
				}
				break;
			case 3: // Ratio of two Uint
				long[] myratio = mxfbuf.getRatio(locpos + 4, datalen);
				String myratiostring = "" + Long.toString(myratio[0]) + ":" + Long.toString(myratio[1]);
				if (!append) {
					attributeMap.put(MapEntry, myratiostring);
				} else {
					attributeMap.put(MapEntry, oldEntry + myratiostring);
				}
				break;
			case 4: // Universal Label (UL)
				if (!append) {
					attributeMap.put(MapEntry, mxfbuf.getUL(locpos + 4, datalen));
				} else {
					attributeMap.put(MapEntry, oldEntry + mxfbuf.getUL(locpos + 4, datalen));
				}
				break;
			default:
				break;
			}
			String currentEntry = attributeMap.get(MapEntry);
			if (!append && currentEntry != null && oldEntry != null && !currentEntry.matches(oldEntry)) {
				// logger.debug("getLocalMDField: "+MapEntry+" has not a single value"+oldEntry+" "+currentEntry);
				attributeMap.put(MapEntry, "multiple property with different values");
			}
		}

	}

	private static int[] initsmptekey(String stringkeyin) {

		int i = 0;
		int[] binkeyout = new int[SMPTEKEYSIZE];

		String tmp;
		StringTokenizer st = new StringTokenizer(stringkeyin);
		while (st.hasMoreTokens() && i < SMPTEKEYSIZE) {
			tmp = "0x" + st.nextToken().trim();
			binkeyout[i] = Integer.decode(tmp);
			i++;
		}
		return (binkeyout);
	}
}

class MXFBuffer {
	public int[] buf = null;
	public int bufreallen;

	public MXFBuffer(int bufsize) {
		buf = new int[bufsize];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = 0;
		}
	}

	public void loadBuffer(RandomAccessFile mxfin) throws ToolException {
		byte[] readbuf = new byte[buf.length];
		try {
			bufreallen = mxfin.read(readbuf, 0, buf.length);
			for (int i = 0; i < bufreallen; i++) {
				// TBC
				buf[i] = (int) readbuf[i] & 0xFF;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ToolException("Error loading MXF buffer...");
		}
	}

	public boolean checksmptekey(int pos, int[] key, int klen) {
		// the key must be exactly at the given position, otherwise return false
		// the key can be masked by
		if (pos + klen > buf.length)
			return false;
		for (int i = 0; i < klen; i++) {
			if (buf[pos + i] != key[i] && key[i] < 256) {
				return false;
			}
		}
		return true;
	}

	public int findsmptekey(int pos, int[] key, int maxsearch, int klen) {
		// returns the buffer position to the location where the key was found
		// otherwise return -1
		int ret = -1;
		for (int i = pos; i < (pos + maxsearch); i++) {
			if (checksmptekey(i, key, klen)) {
				return i;
			}
		}
		return ret;
	}

	public String getUL(int pos, int len) throws ToolException {
		String ret = "";
		String hex = "";
		for (int i = pos; i < pos + len; i++) {
			hex = Integer.toHexString(buf[i]);
			if (hex.length() < 2)
				hex = "0" + hex;
			// ret += Integer.toHexString((byte)buf[i])+" ";
			ret += hex + " ";
		}
		ret = ret.substring(0, ret.length() - 1);// toglie ultimo spazio
		return (ret);
	}

	public long[] getRatio(int pos, int len) throws ToolException {
		long[] ret = new long[2];
		ret[0] = getUint(pos, len / 2);
		ret[1] = getUint(pos + len / 2, len / 2);
		return ret;
	}

	public long getUint(int pos, int len) throws ToolException {
		// read from buffer
		long ret = 0;
		int val = 0;
		for (int i = pos; i < pos + len; i++) {
			// signed to unsigned
			val = ((int) buf[i]) & 0xFF;
			ret = ret * 256 + val;
		}
		return (ret);
	}

	public long getInt(int pos, int len) throws ToolException {
		// legge da buffer in posizione pos, len "bytes" come Signed int
		long ret = 0;
		int val = 0;

		for (int i = pos; i < pos + len; i++) {
			if (i == pos)
				val = ((int) buf[i]);
			else
				val = ((int) buf[i]) & 0xFF;
			ret = ret * 256 + val;
		}
		return (ret);
	}

	public long parseBERlength(int pos) throws ToolException {
		// aims to be according to Annex K of SMPTE 336M-2007
		long len = 0;
		// read 1 byte
		int n = buf[pos];
		if (n < 128) {
			len = n;
		} else {
			n -= 128;
			len = getUint(pos + 1, n);
		}
		return (len);
	}

	public int BERlengthOffset(int pos) throws ToolException {
		int n = buf[pos];
		if (n < 128)
			return (1);
		else
			return (n - 128 + 1);
	}

}
