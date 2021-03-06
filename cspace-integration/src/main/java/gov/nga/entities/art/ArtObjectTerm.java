/*
    NGA Art Data API: An entity term implementation for art objects 
    TODO should review to see if Term should be split out into its
    own class like textentry

    Copyright (C) 2018 National Gallery of Art Washington DC
    Developers: David Beaudet

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package gov.nga.entities.art;

import gov.nga.entities.common.KeyedValue;
import gov.nga.utils.CollectionUtils;
import gov.nga.utils.TypeUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static gov.nga.utils.StringUtils.*;

public class ArtObjectTerm extends ArtEntityImpl implements KeyedValue {
	
	public static enum TERMTYPES {
		KEYWORD, SCHOOL, STYLE, SYSCAT, THEME
	};
	
	private static final Map<String, TERMTYPES> TERMTYPEMAP;
	static {
		Map<String, TERMTYPES> myMap = CollectionUtils.newHashMap();
		myMap.put("Keyword", 						TERMTYPES.KEYWORD);
		myMap.put("School",							TERMTYPES.SCHOOL);
		myMap.put("Style",							TERMTYPES.STYLE);
		myMap.put("Systematic Catalogue Volume",	TERMTYPES.SYSCAT);
		myMap.put("Theme",							TERMTYPES.THEME);
		TERMTYPEMAP = Collections.unmodifiableMap(myMap);
	}

	protected static final String fetchAllObjectTermsQuery = 
		"SELECT ot.termID, ot.objectID, ot.termType, ot.term, " + 
		"ot.visualBrowserTheme, ot.visualBrowserStyle, ot.fingerprint " +
		// due to memory constraints, we're not going to load these at this point in time
		//"u.termMasterID, u.termMasterCN, u.nodeDepth, u.numChildren, " +
		//"u.termHierarchy, u.parentHierarchy " +
		"FROM data.objects_terms ot " +
		"JOIN data.terms_used u ON u.termID = ot.termID ";
	
	private Long objectID = null;
	public Long getObjectID() {
		return objectID;
	}
	
	private Long termID = null;
	public Long getTermID() {
		return termID;
	}
	
	private String termTypeVal = null;
	private String getTermTypeVal() {
		return termTypeVal;
	}

	private TERMTYPES termType = null;
	public TERMTYPES getTermType() {
		return termType;
	}

	private String term = null;
	public String getTerm() {
		return term;
	}

    public void setTerm(String term){
        this.term = term;
    }

    private String visualBrowserTheme = null;
	public String getVisualBrowserTheme() {
		return visualBrowserTheme;
	}

	private String visualBrowserStyle = null;
	public String getVisualBrowserStyle() {
		return visualBrowserStyle;
	}
	
	public ArtObjectTerm(ArtDataManagerService manager) {
		super(manager);
	}
	
	public ArtObjectTerm(ArtDataManagerService manager, ResultSet rs) throws SQLException {
		// pass fingerprint to constructor along with data manager
		super(manager,TypeUtils.getLong(rs, 7));
		termID 		= TypeUtils.getLong(rs, 1);
		objectID 	= TypeUtils.getLong(rs, 2);
		termTypeVal = rs.getString(3);
		term		= htmlToMarkdown(sanitizeHtml(rs.getString(4)));
		visualBrowserTheme = rs.getString(5);
		visualBrowserStyle = rs.getString(6);
		termType = TERMTYPEMAP.get(getTermTypeVal());
	}
	
	public ArtObjectTerm factory(ResultSet rs) throws SQLException {
		ArtObjectTerm at = new ArtObjectTerm(getManager(),rs);
		return at;
	}
	
	public String getKeyValue() {
		return getTermID().toString() + "; " + getObjectID().toString();
	}
	
	public boolean isTheme() {
		return (getTermType().equals(TERMTYPES.THEME));
	}

	public boolean isKeyword() {
		return (getTermType().equals(TERMTYPES.KEYWORD));
	}
	
	public boolean isSchool() {
		return (getTermType().equals(TERMTYPES.SCHOOL));
	}

	public boolean isStyle() {
		return (getTermType().equals(TERMTYPES.STYLE));
	}

	public boolean isSysCat() {
		return (getTermType().equals(TERMTYPES.SYSCAT));
	}

}