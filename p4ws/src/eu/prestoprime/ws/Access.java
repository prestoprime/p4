/**
 * Access.java
 * Author: Francesco Gallo (gallo@eurix.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 EURIX Srl, Torino, Italy
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
package eu.prestoprime.ws;

import it.eurix.archtools.data.DataException;
import it.eurix.archtools.data.model.DIP;
import it.eurix.archtools.data.model.DIP.DCField;
import it.eurix.archtools.data.model.IPException;
import it.eurix.archtools.persistence.DatabaseException;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.transform.dom.DOMSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.core.HttpContext;

import eu.prestoprime.conf.ConfigurationManager;
import eu.prestoprime.conf.P4PropertyManager.P4Property;
import eu.prestoprime.datamanagement.P4DataManager;
import eu.prestoprime.datamanagement.P4PersistenceManager.P4Collection;

@Path("/access")
public class Access {

	@Context
	HttpContext context;

	private static final Logger logger = LoggerFactory.getLogger(Access.class);

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/list")
	public Response getAllDIP() {

		logger.debug("Called getAllDIP");

		ResponseBuilder rb;
		StringBuffer sb = new StringBuffer();

		List<String> aipList;
		try {
			aipList = P4DataManager.getInstance().getAllAIP(null);

			for (String aipId : aipList)
				sb.append(aipId + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/list/{type}")
	public Response getAllDIPByMD(@PathParam("type") String datatype, @QueryParam("available") String available) {

		logger.debug("Called getAllDIPByMD");

		if (available == null || available.isEmpty())
			available = "true";

		ResponseBuilder rb;
		StringBuffer sb = new StringBuffer();

		List<String> aipList;
		try {
			aipList = P4DataManager.getInstance().getAIPByMD(datatype, Boolean.parseBoolean(available));

			for (String aipID : aipList)
				sb.append(aipID + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/checkdcid/{id}")
	public Response checkIdentifier(@PathParam("id") String dcID) {

		ResponseBuilder rb;

		try {
			String aipID = P4DataManager.getInstance().getAIPByDCID(dcID);

			rb = Response.status(Status.OK).entity(aipID != null ? aipID : "available");
		} catch (DataException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/dip/{id}")
	public Response getDIP(@PathParam("id") String dipId) {

		logger.debug("Called getDIP");

		ResponseBuilder rb;

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);

			rb = Response.status(Status.OK).entity(dip.toString());
		} catch (Exception e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/{type}")
	public Response checkDIPByMD(@PathParam("type") String datatype, @PathParam("id") String dipId) {

		logger.debug("Called checkDIPByMD");

		ResponseBuilder rb;
		StringBuffer sb = new StringBuffer();

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);

			sb.append(dip.hasDatatype(datatype));

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException | IPException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/{type}/result")
	public Response getMDResourceByID(@PathParam("id") String dipID, @PathParam("type") String datatype) {

		logger.debug("Called getMDResourceByID");

		ResponseBuilder rb;
		StringBuffer sb = new StringBuffer();

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipID);

			for (String result : dip.getMDResource(datatype)) {
				sb.append(result + "\n");
			}

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException | IPException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/preview")
	public Response getDIPPreview(@PathParam("id") String dipId) {

		logger.debug("Called getDIPPreview");

		ResponseBuilder rb;

		String browsingFormats = ConfigurationManager.getPropertyInstance().getProperty(P4Property.BROWSING_QUALITY_FORMATS);
		String[] lqFormats = browsingFormats.split(",");

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < lqFormats.length; i++) {
				for (String videoHRef : dip.getAVMaterial(lqFormats[i], "URL")) {
					sb.append(lqFormats[i] + "\t" + videoHRef + "\n");

					logger.debug("Requested video URL: " + videoHRef + " (" + lqFormats[i] + ")");
				}
			}
			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException | IPException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();

	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/thumb")
	public Response getDIPThumb(@PathParam("id") String dipId) {

		logger.debug("Called getDIPThumb");

		ResponseBuilder rb;

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);
			URL thumb = dip.getThumbnail();

			rb = Response.status(Status.OK).entity(thumb.toString());
		} catch (DataException | IPException e) {
			logger.warn(e.getMessage());
			// e.printStackTrace(); I removed this to reduce logging. P
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/dip/{id}/info")
	public Response findDcRecordsByAIPId(@PathParam("id") String dipId) {

		ResponseBuilder rb;

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);
			StringBuffer sb = new StringBuffer();

			Map<String, List<String>> dcFields = dip.getDCFields();

			for (String key : dcFields.keySet()) {
				sb.append(key + "\t");
				for (String field : dcFields.get(key)) {
					sb.append(field + "\t");
				}
				sb.append("\n");
			}

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException | IPException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();

	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/info/container")
	public Response checkContainer(@PathParam("id") String dipId) {

		logger.debug("Called checkContainer");

		ResponseBuilder rb;

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);
			StringBuffer sb = new StringBuffer();

			for (String format : dip.getDCField(DCField.format))
				sb.append(format + "\n");

			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (DataException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		} catch (IPException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();

	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/info/dracma")
	public Response checkMaterialID(@PathParam("id") String dipId) {

		logger.debug("Called checkMaterialID");

		ResponseBuilder rb;

		String masterFormats = ConfigurationManager.getPropertyInstance().getProperty(P4Property.MASTER_QUALITY_FORMATS);
		String[] mqFormats = masterFormats.split(",");

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mqFormats.length; i++) {
				for (String videoURN : dip.getAVMaterial(mqFormats[i], "URN")) {
					if (videoURN.contains("dracma"))
						sb.append(videoURN.substring(videoURN.lastIndexOf(":") + 1) + "\n");
				}
			}
			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (IPException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	/**
	 * Used by rights tool
	 * 
	 * @param dipId
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/dip/{id}/preview/player")
	public Response getQuickPlayer(@PathParam("id") String dipId) {

		ResponseBuilder rb;

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);

			String downloadVideo = dip.getAVMaterial("video/webm", "URL").get(0);
			URL downloadIcon = dip.getThumbnail();

			StringBuffer sb = new StringBuffer();
			sb.append("<html>\n");
			sb.append("<body style=\"background-color:black\">\n");
			sb.append("<center>\n");
			sb.append("<video controls width=\"360\" height=\"288\" poster=" + downloadIcon + " />\n");
			sb.append("<source  src=\"" + downloadVideo + "\" />\n");
			sb.append("Your browser does not support the video codec.\n");
			sb.append("</center>\n");
			sb.append("</body>\n");
			sb.append("</html>");

			rb = Response.status(Status.OK).entity(sb.toString());

		} catch (IPException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	/**
	 * Used by access interface
	 * 
	 * @param dipId
	 * @return Returns list of URLs for all key frames
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/frames")
	public Response getFrames(@PathParam("id") String dipId) {

		logger.debug("Called getFrames");

		ResponseBuilder rb;

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);
			List<URL> frames = dip.getFrames();

			StringBuffer sb = new StringBuffer();
			for (URL frame : frames)
				sb.append(frame + "\n");
			rb = Response.status(Status.OK).entity(sb.toString());
		} catch (IPException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();

	}

	@GET
	@Path("/resource/{collection}/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getResource(@PathParam("collection") String collection, @PathParam("id") String resId) {
		logger.info("Called /access/resource/" + collection + "/" + resId);

		ResponseBuilder rb;

		Node resource;
		try {
			resource = P4DataManager.getInstance().getResource(P4Collection.getP4Collection(collection), resId);
			DOMSource source = new DOMSource(resource);

			rb = Response.status(Status.OK).entity(source);
		} catch (DataException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		} catch (DatabaseException e) {
			e.printStackTrace();
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dip/{id}/rights/graph")
	public Response getOWLGraph(@PathParam("id") String dipId) {

		logger.debug("Called getOWLGraph");

		ResponseBuilder rb;

		try {
			DIP dip = P4DataManager.getInstance().getDIPByID(dipId);
			URL url = dip.getGraph();
			rb = Response.status(Status.OK).entity(url.toString());
		} catch (IPException e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			rb = Response.status(Status.INTERNAL_SERVER_ERROR);
		}

		return rb.build();
	}

}