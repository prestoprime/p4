/**
 * AdminActions.java
 * Author: Francesco Rosso (rosso@eurix.it)
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
package eu.prestoprime.p4gui.util.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AdminActions {

	public static enum STATUS_MAPPING {
		OK, WARNING, CRITICAL
	};

	public static enum QA_STATUS {
		QA_NA(STATUS_MAPPING.CRITICAL), QA_AUTO(STATUS_MAPPING.WARNING), QA_VALIDATED(STATUS_MAPPING.OK);

		private STATUS_MAPPING mapping;

		private QA_STATUS(STATUS_MAPPING mapping) {
			this.mapping = mapping;
		}

		public STATUS_MAPPING getMapping() {
			return mapping;
		}
	};

	public static enum FPRINT_STATUS {
		FPRINT_NA(STATUS_MAPPING.CRITICAL), FPRINT_OK(STATUS_MAPPING.OK);

		private STATUS_MAPPING mapping;

		private FPRINT_STATUS(STATUS_MAPPING mapping) {
			this.mapping = mapping;
		}

		public STATUS_MAPPING getMapping() {
			return mapping;
		}
	}

	public static enum FIXITY_STATUS {
		FIXITY_NA(STATUS_MAPPING.CRITICAL), FIXITY_OLD(STATUS_MAPPING.WARNING), FIXITY_OK(STATUS_MAPPING.OK);

		private STATUS_MAPPING mapping;

		private FIXITY_STATUS(STATUS_MAPPING mapping) {
			this.mapping = mapping;
		}

		public STATUS_MAPPING getMapping() {
			return mapping;
		}
	}

	private Map<String, Action> actions;

	public AdminActions() {
		actions = new HashMap<>();
	}

	public void addAction(String line) {
		String[] fields = line.split("\\t");

		String id = fields[0];

		Action action = new Action();
		for (int i = 1; i < fields.length; i++) {
			try {
				action.setStatus(QA_STATUS.valueOf(fields[i]));
			} catch (Exception e1) {
				try {
					action.setStatus(FPRINT_STATUS.valueOf(fields[i]));
				} catch (Exception e2) {
					try {
						action.setStatus(FIXITY_STATUS.valueOf(fields[i]));
					} catch (Exception e3) {

					}
				}
			}
		}

		actions.put(id, action);
	}

	public Set<String> keySet() {
		return actions.keySet();
	}

	public Action getAction(String id) {
		return actions.get(id);
	}

	public class Action {

		private QA_STATUS qaStatus;
		private FPRINT_STATUS fprintStatus;
		private FIXITY_STATUS fixityStatus;

		public Action() {
			qaStatus = QA_STATUS.QA_NA;
			fprintStatus = FPRINT_STATUS.FPRINT_NA;
			fixityStatus = FIXITY_STATUS.FIXITY_NA;
		}

		public void setStatus(QA_STATUS qaStatus) {
			this.qaStatus = qaStatus;
		}

		public void setStatus(FPRINT_STATUS fprintStatus) {
			this.fprintStatus = fprintStatus;
		}

		public void setStatus(FIXITY_STATUS fixityStatus) {
			this.fixityStatus = fixityStatus;
		}

		public QA_STATUS getQaStatus() {
			return qaStatus;
		}

		public FPRINT_STATUS getFprintStatus() {
			return fprintStatus;
		}

		public FIXITY_STATUS getFixityStatus() {
			return fixityStatus;
		}
	}
}
