package gov.nga.entities.art;

import gov.nga.entities.art.TextEntry.TEXT_ENTRY_TYPE;
import gov.nga.entities.art.factory.ArtObjectFactory;
import gov.nga.search.SearchFilter;
import gov.nga.search.SortHelper;
import gov.nga.utils.CollectionUtils;
import gov.nga.utils.StringUtils;
import gov.nga.utils.TypeUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nga.utils.stringfilter.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gov.nga.utils.StringUtils.*;


public class Constituent extends ArtEntityImpl
{

	private static final Logger log = LoggerFactory.getLogger(Constituent.class);

	static public final String JCRBasePath = "/content/nga/tms/Constituent";
	
	// consistent facets fields
	public static enum FACET {
		INDEXOFARTISTS_FIRST_NORMALIZED_LETTER_LAST_NAME,
		INDEXOFARTISTS_FIRST_TWO_LETTERS_LAST_NAME,
		INDEXOFARTISTS_LETTER_RANGE, 
		VISUALBROWSERNATIONALITY, 
		VISUALBROWSERTIMESPAN,
		HASBIOGRAPHY,
		WORKS_RELATIONSHIPS,
		OSCI_CATALOGUE
	}

	// constituent search fields
	public static enum SEARCH {
		PREFERRED_DISPLAY_NAME,
		ALL_NAMES,
		ISINDIVIDUALARTIST,
		ISARTISTOFNGAOBJECT,
		VISUALBROWSERNATIONALITY,
		VISUALBROWSERTIMESPAN,
		//INDEXOFARTISTS_FIRST_NORMALIZED_LETTER_LAST_NAME,
		INDEXOFARTISTS_FIRST_TWO_LETTERS_LAST_NAME,
		INDEXOFARTISTS_LETTER_RANGE,
		HASBIOGRAHPY,
		YEARS_SPAN,
		CONSTITUENT_ID,
		ULAN_ID
	}

	public static enum FREETEXTSEARCH {
		ALLDATAFIELDS
	}

	// constituent sort fields
	public static enum SORT {
		// for comparing two constituents with a third base constituent
		PREFERRED_DISPLAY_NAME_MATCH,
		HASBIOGRAPHY_MATCH,

		// for comparing two constituents with each other 
		PREFERRED_DISPLAY_NAME_ASC,
		HASBIOGRAPHY_ASC
	}

	private static final String JCRNODENAME = "Constituent";
	
	private String indexOfArtistsRange = null;
	private String preferredDisplayNameStartsWith = null;

	// types of relationships between an artist and objects
	
	private static enum ARTISTWORKRELATIONS {
		RELATEDWORKS (ArtObjectConstituent.RELATEDARTISTROLE,"relatedworks"),
		AFTERWORKS	 (ArtObjectConstituent.AFTERWORKSROLE,   "afterworks"),
		WORKSBYARTIST(null,                                  "worksbyartist"),
		ALLWORKS	 (null,                                  null);
		
		private String label = null;
		private String dataLabel = null;
		private ARTISTWORKRELATIONS(String dataLabel, String label) {
			this.label = label;
			this.dataLabel = dataLabel;
		}
		
		public String getLabel() {
			return label;
		}
		
		public String getDataLabel() {
			return dataLabel;
		}
		
		public boolean matchesDataLabel(String dataLabel) {
			if (this.equals(ALLWORKS))
				return true;
			if (this.equals(WORKSBYARTIST)) {
				// if this data label does not match related works or afterworks
				// then it's a work by the artist
				return !RELATEDWORKS.matchesDataLabel(dataLabel) && !AFTERWORKS.matchesDataLabel(dataLabel);
			}
			else if (getDataLabel() != null) {
				return getDataLabel().equals(dataLabel);
			}
			return false;
		}
		
		public static ARTISTWORKRELATIONS fromLabel(String label) {
			if (WORKSBYARTIST.getLabel().equals(label))
				return WORKSBYARTIST;
			if (RELATEDWORKS.getLabel().equals(label))
				return RELATEDWORKS;
			if (AFTERWORKS.getLabel().equals(label))
				return AFTERWORKS;
			return ALLWORKS;
		}
	}
	
	

	public Constituent(ArtDataManagerService manager) {
		super(manager);
	}

	public Constituent factory(ResultSet rs) throws SQLException {
		Constituent c = new Constituent(getManager(),rs);
		return c;
	}

	protected static final String fetchAllConstituentsQuery = 
		"SELECT fingerprint, constituentID, preferredDisplayName, forwardDisplayName, " + 
		"       displayDate, artistOfNGAObject, beginYear, endYear, nationality, " +
		"       visualBrowserNationality, visualBrowserTimeSpan, constituentType, " +
		"		lastName, biography, constituentLeonardoID, ULANID " +
		"FROM data.constituents ";

	protected static final String briefConstituentQuery =
		fetchAllConstituentsQuery + "WHERE constituentID @@ ";

	public Constituent(ArtDataManagerService manager, ResultSet rs) throws SQLException {
		super(manager,		  	      TypeUtils.getLong(rs, 1));
		constituentID 				= TypeUtils.getLong(rs, 2);
		preferredDisplayName 		= rs.getString(3);
		forwardDisplayName 			= rs.getString(4);
		displayDate					= rs.getString(5);
		artistOfNGAObject			= TypeUtils.getLong(rs, 6);
		beginYear					= TypeUtils.getLong(rs, 7);
		endYear						= TypeUtils.getLong(rs, 8);
		nationality					= rs.getString(9);
		visualBrowserNationality 	= rs.getString(10);
		visualBrowserTimeSpan		= rs.getString(11);
		constituentType				= rs.getString(12);
		lastName					= rs.getString(13);
		biography 					= htmlToMarkdown(sanitizeHtml(rs.getString(14)));
		constituentLeonardoID		= rs.getString(15);
		ulanID 						= rs.getString(16);
	}
	
	protected Constituent(Constituent source) throws SQLException {
		super(source.getManager(), source.getFingerprint());
		artistOfNGAObject			= source.artistOfNGAObject;
		beginYear					= source.beginYear;
		biography 					= source.biography;
		constituentID 				= source.constituentID;
		constituentLeonardoID		= source.constituentLeonardoID;
		constituentType				= source.constituentType;
		displayDate					= source.displayDate;
		endYear						= source.endYear;
		lastName					= source.lastName;
		preferredDisplayName 		= source.preferredDisplayName;
        forwardDisplayName          = source.forwardDisplayName;
		nationality					= source.nationality;
		ulanID 						= source.ulanID;
		visualBrowserNationality 	= source.visualBrowserNationality;
		visualBrowserTimeSpan		= source.visualBrowserTimeSpan;
		
		this.altnames				= source.altnames;
		this.indexOfArtistsRange	= source.indexOfArtistsRange;
		this.objectRoles			= source.objectRoles;
		this.ownerRoles				= source.ownerRoles;
		this.preferredDisplayNameStartsWith	= source.preferredDisplayNameStartsWith;
		this.textEntries			= source.textEntries;
		this.works					= source.works;
		this.worksRoles				= source.worksRoles;
		
	}
	
	public void setAdditionalProperties(ResultSet rs) throws SQLException {
		constituentLeonardoID = rs.getString(1);
	}

	public Boolean isIndividualArtist() {
		String cType = getConstituentType();
		return 	isArtistOfNGAObject() 
				&&	( 
						cType.equals("individual") || 
						cType.equals("couple") 
				);
	}

	// used for searches
	public Boolean matchesFilter(SearchFilter f) {
		switch ( (SEARCH) f.getField()) {
	//	case INDEXOFARTISTS_FIRST_NORMALIZED_LETTER_LAST_NAME: 
	//		return f.filterMatch(isIndividualArtist() ? getLastNameFirstLetterNormalized() : null);
		case INDEXOFARTISTS_FIRST_TWO_LETTERS_LAST_NAME: 
			return f.filterMatch(isIndividualArtist() ? getPreferredDisplayNameStartsWith() : null);
		case INDEXOFARTISTS_LETTER_RANGE: 
			return f.filterMatch(isIndividualArtist() ? getIndexOfArtistsRange(): null);
		case ISARTISTOFNGAOBJECT: 
			return f.filterMatch(isArtistOfNGAObject().toString());
		case ISINDIVIDUALARTIST: 
			return f.filterMatch(isIndividualArtist().toString());
		case PREFERRED_DISPLAY_NAME: 
			return f.filterMatch(getPreferredDisplayName());
		case CONSTITUENT_ID:
			return f.filterMatch(getConstituentID().toString());
		case ULAN_ID:
			return f.filterMatch(getULANID());
		case ALL_NAMES:
			if (f.filterMatch(getPreferredDisplayName()))
				return true;
			if (getAltNamesRaw() != null) {
				for (ConstituentAltName alt : getAltNamesRaw()) {
					if (f.filterMatch(alt.getDisplayName()))
						return true;
				}
			}
			return false;
		case VISUALBROWSERNATIONALITY: 
			return f.filterMatch(getVisualBrowserNationality());	
		case VISUALBROWSERTIMESPAN: 
			return f.filterMatch(getVisualBrowserTimeSpan());
		case HASBIOGRAHPY:
			return f.filterMatch(hasBiography().toString());
		case YEARS_SPAN:
			Long by = getBeginYear();
			Long ey = getEndYear();
			String v1 = ( ( by == null ) ? null : by.toString() );
			String v2 = ( ( ey == null ) ? null : ey.toString() );
			return f.filterMatch(v1,v2);
		}
		return false;
	}

	// used for facets
	public List<String> getFacetValue(Object f) {
		switch ((FACET) f) {
		case INDEXOFARTISTS_FIRST_NORMALIZED_LETTER_LAST_NAME:
			return isIndividualArtist() ? StringUtils.stringToList(getLastNameFirstLetterNormalized()) : StringUtils.stringToList(null);
		case INDEXOFARTISTS_LETTER_RANGE: 
			return isIndividualArtist() ? StringUtils.stringToList(getIndexOfArtistsRange()) : StringUtils.stringToList(null);
		case INDEXOFARTISTS_FIRST_TWO_LETTERS_LAST_NAME: 
			return isIndividualArtist() ? StringUtils.stringToList(getPreferredDisplayNameStartsWith()) : StringUtils.stringToList(null);
		case VISUALBROWSERNATIONALITY: 
			return StringUtils.stringToList(getVisualBrowserNationality());
		case VISUALBROWSERTIMESPAN: 
			return StringUtils.stringToList(getVisualBrowserTimeSpan());
		case HASBIOGRAPHY:
			return StringUtils.stringToList(hasBiography().toString());
		case WORKS_RELATIONSHIPS:
			return getWorksRelationships();
		case OSCI_CATALOGUE:
			break;
		default:
			break;
		} 
		return null;
	}


	private synchronized void setIndexOfArtistRange(String s) {
		indexOfArtistsRange = s;
	}

	public String getIndexOfArtistsRange() {
		if (indexOfArtistsRange == null) {
			if (!isIndividualArtist()) {
				setIndexOfArtistRange("");
				return indexOfArtistsRange;
			}

			Map<String, String> m = getManager().getIndexOfArtistsRanges();
			for (String s : m.keySet()) {
				String e = m.get(s);
				String v = getPreferredDisplayNameStartsWith();

				Integer c1 = SortHelper.compareObjectsDiacritical(s, v);
				Integer c2 = SortHelper.compareObjectsDiacritical(e, v);

				if (c1 == null)
					c1 = 0;
				if (c2 == null)
					c2 = 0;


				if (c1 <= 0 && c2 >= 0) {
					setIndexOfArtistRange(s + " to " + e);
					return indexOfArtistsRange;
				}
			}
			setIndexOfArtistRange("");
		}
		return indexOfArtistsRange;
	}

	
	public Long matchesAspect(Object ae, Object order) {
		Constituent c = (Constituent) ae;
		if (c == null || order == null)
			return null;
		switch ((SORT) order) {
		case PREFERRED_DISPLAY_NAME_MATCH: 
			if (getPreferredDisplayName() == null || c.getPreferredDisplayName() == null)
				return null;
			return new Long(getPreferredDisplayName().equals(c.getPreferredDisplayName()) ? 1 : 0);
		case HASBIOGRAPHY_MATCH:
			return new Long(hasBiography() == c.hasBiography() ? 1 : 0);
		case HASBIOGRAPHY_ASC:
			break;
		case PREFERRED_DISPLAY_NAME_ASC:
			break;
		default:
			break;
		}
		return null;
	}

	// USED FOR SORTING
	// determine whether not the given constituent matches this constituent
	// in any of a fixed number of dimensions
	// returns 1 if a match is found, 0 if one is not found
	// or null if a comparison cannot be made on the given dimension
	public Integer aspectScore(Object ae, Object order, String matchString) {

		Constituent c = (Constituent) ae;

		if (c == null || order == null)
			return null;
		switch ((SORT) order) {
		case PREFERRED_DISPLAY_NAME_ASC: 
			return SortHelper.compareObjectsDiacritical(getPreferredDisplayName(), c.getPreferredDisplayName());
		case HASBIOGRAPHY_ASC:
			// we need to reverse this because we want biographies first
			int a = hasBiography() ? 0 : 1;
			int b = c.hasBiography() ? 0 : 1;
			return SortHelper.compareObjects(a,b);
		case HASBIOGRAPHY_MATCH:
			break;
		case PREFERRED_DISPLAY_NAME_MATCH:
			break;
		default:
			break;
		}
		return null;
	}

	public String freeTextSearchToNodePropertyName(Object field) {
		switch ( (FREETEXTSEARCH) field ) {
		case ALLDATAFIELDS 			: return "*";
		}
		return null;
	}

	synchronized protected void setObjectRoles(List<ArtObjectConstituent> newObjectRoles) {
		objectRoles = newObjectRoles;
		loadWorksRoles();
	}

	private void loadWorksRoles() {
		List<ArtObjectConstituent> newWorksRoles = CollectionUtils.newArrayList();
		List<ArtObjectConstituent> newOwnerRoles = CollectionUtils.newArrayList();
		for (ArtObjectConstituent oc : getObjectRolesRaw() ) {
			if (oc.getRoleType().equals(ArtObjectConstituent.ARTISTROLETYPE)) {
				newWorksRoles.add(oc);
			}
			else if (  oc.getRoleType().equals(ArtObjectConstituent.OWNERROLETYPE)
					&& oc.getRole().equals(ArtObjectConstituent.PREVIOUSOWNERROLE)) {
				newOwnerRoles.add(oc);
			}
		}
		worksRoles = newWorksRoles;
		ownerRoles = newOwnerRoles;
		
		// pre-cache the lists of works since the work roles have changed
		segmentWorks();
	}

	private Map<ARTISTWORKRELATIONS, List<Long>> works = CollectionUtils.newHashMap();
	private void setWorks(ARTISTWORKRELATIONS relationship, List<Long> works) {
		synchronized(works) {
			this.works.put(relationship, works);
		}
	}

	// segment works relationships into lists of each relationship type 
	private void segmentWorks() {
		for (ARTISTWORKRELATIONS relation : EnumSet.allOf(ARTISTWORKRELATIONS.class)) {
			// we don't already have the list of works cached for this relationship type
			List<ArtObjectConstituent> wkroles = getWorksRolesRaw();

			// build a list of the object IDs we need from the list of work roles
			Map<Long,Object> uniqueIDs = CollectionUtils.newHashMap();
			for (ArtObjectConstituent oc : wkroles) {
				if (relation.matchesDataLabel(oc.getRole())) {
					uniqueIDs.put(oc.getObjectID(), null);
				}
			}

			List<Long> ids = CollectionUtils.newArrayList(uniqueIDs.keySet());
			setWorks(relation, ids);
		}
	}

	// relationLabels should be a set of Strings from relations.getLabel() 
	public Set<Long> getWorksIDs(String... relationLabels) {
		
		// default to null string which means all works
		if (relationLabels.length == 0) {
			relationLabels = new String[1];
			relationLabels[0] = ARTISTWORKRELATIONS.ALLWORKS.getLabel();
		}
		Set<Long> uniqueIDs = CollectionUtils.newHashSet();
		for (String val : relationLabels) {
			ARTISTWORKRELATIONS relation = ARTISTWORKRELATIONS.fromLabel(val);
			List<Long> ids = works.get(relation);
			if (ids != null)
				uniqueIDs.addAll(ids);
		}
		
		return uniqueIDs;
	}

	// relationLabels should be a set of Strings from relations.getLabel() 
	public List<ArtObject> getWorks(String... relationLabels) {
		Collection<Long> uniqueIDs = getWorksIDs(relationLabels);
        try {
            return getManager().fetchByObjectIDs(uniqueIDs);
        } catch (DataNotReadyException exception) {
            log.error("Can not fetch By Object ID:" + exception.getMessage());
        }
        return CollectionUtils.newArrayList();
    }
	
	public <T extends ArtObject>List<T> getWorks(ArtObjectFactory<T> factory, String... relationLabels)
	{
		List<T> works = CollectionUtils.newArrayList();
		Collection<Long> uniqueIDs = getWorksIDs(relationLabels);
        try {
            for (ArtObject object: getManager().fetchByObjectIDs(uniqueIDs))
            {
                works.add(factory.createArtObject(object));
            }
        } catch (DataNotReadyException exception) {
            log.error("Can not fetch By Object IDs:" + exception.getMessage());
        }
        return works;
	}

	public List<String> getWorksRelationships() {
		List<String> l = CollectionUtils.newArrayList();
		for (ARTISTWORKRELATIONS rel : works.keySet()) {
			String st = rel.getLabel();
			// don't bother sending the all works label value which is null
			if (st != null)
				l.add(st);
		}
		return l;
	}

	public List<ArtObjectConstituent> getObjectRoles() {
		return CollectionUtils.newArrayList(getObjectRolesRaw());
	}

	private List<ArtObjectConstituent> objectRoles = null;
	private List<ArtObjectConstituent> getObjectRolesRaw() {
		if (objectRoles != null)
			return objectRoles;
		return CollectionUtils.newArrayList();
	}

	// artist roles that this constituent has with objects
	// in the NGA collection
	public List<ArtObjectConstituent> getWorksRoles() {
		return CollectionUtils.newArrayList(getWorksRolesRaw());
	}

	private List<ArtObjectConstituent> worksRoles = null;
	private List<ArtObjectConstituent> getWorksRolesRaw() {
		if (worksRoles != null)
			return worksRoles;
		return CollectionUtils.newArrayList();
	}

	// previous owner roles that this constituent has with objects
	// in the NGA collection
	public List<ArtObjectConstituent> getOwnerRoles() {
		return CollectionUtils.newArrayList(getOwnerRolesRaw());
	}

	private List<ArtObjectConstituent> ownerRoles = null;
	private List<ArtObjectConstituent> getOwnerRolesRaw() {
		if (ownerRoles != null)
			return ownerRoles;
		return CollectionUtils.newArrayList();
	}
	public Boolean isPreviousOwnerOfNGAObject() {
		return ( ownerRoles != null && ownerRoles.size() > 0);
	}
	
	private List<ConstituentTextEntry> textEntries = null;
	private List<ConstituentTextEntry> getTextEntriesRaw() {
		return textEntries;
	}

	public List<Bibliography> getBibliography() {
    	List<Bibliography> bList = CollectionUtils.newArrayList();
        List<Bibliography> bListNilYears = null;
    	for (ConstituentTextEntry ce : TextEntry.filterByTextType(getTextEntriesRaw(), TEXT_ENTRY_TYPE.BIBLIOGRAPHY)) {
    		if ( ce instanceof ConstituentBibliography)
            {
                ConstituentBibliography cb = (ConstituentBibliography) ce;
                cb.setText(htmlToMarkdown(sanitizeHtml(cb.getText())));

                //Add bibliography entries with unknown year to different list
                //and then push all of them at the beginning of the result list
                if (cb.getYearPublished() != 0L) {
                    bList.add( cb );
                } else {
                    if (bListNilYears == null) {
                        bListNilYears = CollectionUtils.newArrayList();
                    }
                    bListNilYears.add(cb);
                }
            }
    	}

        if (bListNilYears != null) {
            bList.addAll(0, bListNilYears);
        }
    	return bList;
	}

	synchronized protected void addTextEntry(ConstituentTextEntry te) {
		if (textEntries == null)
			textEntries = CollectionUtils.newArrayList();
		textEntries.add(te);
	}

	public List<ConstituentAltName> getAltNames() {
		// return a copy of the alt names
		return CollectionUtils.newArrayList(getAltNamesRaw());
	}

	private List<ConstituentAltName> altnames = null;
	private List<ConstituentAltName> getAltNamesRaw() {
		return altnames;
	}
	synchronized protected void addAltName(ConstituentAltName alt) {
		if (altnames == null)
			altnames = CollectionUtils.newArrayList();
		altnames.add(alt);
	}

	// BASIC ATTRIBUTES

	private Long constituentID = null;
	public Long getConstituentID() {
		return constituentID;
	}

	public Long getEntityID() {
		return getConstituentID();
	}

	private Long artistOfNGAObject = null;
	private Long getArtistOfNGAObject() {
		return artistOfNGAObject;
	}

	public Boolean isArtistOfNGAObject() {
		return TypeUtils.longToBoolean(getArtistOfNGAObject());
	}

	private Long beginYear = null;
	public Long getBeginYear() {
		return beginYear;
	}

	private Long endYear = null;
	public Long getEndYear() {
		return endYear;
	}

	private String preferredDisplayName = null;
	public String getPreferredDisplayName() {
		return preferredDisplayName;
	}

	private String forwardDisplayName = null;
	public String getForwardDisplayName() {
		return forwardDisplayName;
	}

	private String lastName = null;
	public String getLastName() {
		return lastName;
	}

	private synchronized void calculatePreferredDisplayNameStartsWith() {
		String ln = getPreferredDisplayName();
		String lnsw = null;
		if (ln != null) {
			ln = ln.toLowerCase();
			int idx = 0;
			while (( lnsw == null || lnsw.length() < 2 ) && idx < ln.length() ) {
				int c = ln.codePointAt(idx);
				if (Character.isLetter(c)) {
					String val = String.copyValueOf(Character.toChars(c));
					lnsw = lnsw == null ? val : lnsw + val; 
				}
				idx++;
			}
		}
		preferredDisplayNameStartsWith = lnsw;
	}

	private String getLastNameFirstLetterNormalized() {
		String last2 = getPreferredDisplayNameStartsWith();
		if (last2 == null)
			return null;
		last2 = StringUtils.removeDiacritics(last2);
		if (last2.length() > 1)
			last2 = last2.substring(0, 1);
		return last2;
	}
	
	public String getPreferredDisplayNameStartsWith() {
		if (preferredDisplayNameStartsWith == null)
			calculatePreferredDisplayNameStartsWith();
		return preferredDisplayNameStartsWith;
	}

	private String displayDate = null;
	public String getDisplayDate() {
		return displayDate;
	}

	private String nationality = null;
	public String getNationality() {
		return nationality;
	}

	private String visualBrowserNationality = null;
	public String getVisualBrowserNationality() {
		return visualBrowserNationality;
	}

	private String visualBrowserTimeSpan = null;
	public String getVisualBrowserTimeSpan() {
		return visualBrowserTimeSpan;
	}

	private String constituentType = null;
	public String getConstituentType() {
		return constituentType;
	}

	private String biography = null;
	public String getBiography() {
		return getBiography(getDefaultFilter());
	}
    public String getBiography(StringFilter sf) {
        return sf.getFilteredString(biography);
    }

	public Boolean hasBiography() {
		return getBiography() != null;
	}

	// DETAILS
	private String constituentLeonardoID = null;
	public String getConstituentLeonardoID() {
		//loadDetails();
		return constituentLeonardoID;
	}

	private String ulanID = null;
	public String getULANID() {
		return ulanID;
	}

	public String getJCREntityType() 
	{
		return JCRNODENAME;
	}
	
}


















