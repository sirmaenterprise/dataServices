package gov.nga.entities.art;

import gov.nga.entities.art.ArtDataManager.Suggestion;
import gov.nga.entities.art.factory.ArtObjectFactory;
import gov.nga.entities.art.factory.ConstituentFactory;
import gov.nga.search.Facet;
import gov.nga.search.FacetHelper;
import gov.nga.search.FreeTextSearchable;
import gov.nga.search.ResultsPaginator;
import gov.nga.search.SearchHelper;
import gov.nga.search.SortHelper;
import gov.nga.utils.ConfigService;
import gov.nga.utils.db.DataSourceService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArtDataManagerService {
	
	// JDBC Pool Service 
	public DataSourceService getDataSourceService();
	
	// Art Location Services
	public Location fetchByLocationID(long locationID);
	public List<Location> fetchByLocationIDs(List<Long> locationIDs);
	// public Map<String, String> getAllLocationDescriptionsByRoom();

	// Art Object Services
	public ArtObject       				fetchByObjectID (long objectID) throws DataNotReadyException;
	public <T extends ArtObject>T 		fetchByObjectID (long objectID, ArtObjectFactory<T> factory) throws DataNotReadyException;
	public List<ArtObject>       		fetchByObjectIDs(Collection<Long> objectIDs) throws DataNotReadyException;
	public <T extends ArtObject>List<T> fetchByObjectIDs(Collection<Long> objectIDs, ArtObjectFactory<T> factory) throws DataNotReadyException;
	public List<ArtObject>       		fetchByObjectIDs(Collection<Long> objectIDs, gov.nga.entities.art.ArtObject.SORT... order) throws DataNotReadyException;
	public <T extends ArtObject>List<T> fetchByObjectIDs(Collection<Long> objectIDs, ArtObjectFactory<T> factory, gov.nga.entities.art.ArtObject.SORT... order) throws DataNotReadyException;
	public List<ArtObject>       		fetchObjectsByRelationships(List<ArtObjectConstituent> ocs);
	public <T extends ArtObject>List<T> fetchObjectsByRelationships(List<ArtObjectConstituent> ocs, ArtObjectFactory<T> factory);
	public List<ArtObject> 				searchArtObjects(SearchHelper<ArtObject> sh, ResultsPaginator pn, FacetHelper fn, Object... order) throws DataNotReadyException;
	public <T extends ArtObject>List<T> searchArtObjects(SearchHelper<T> sh, ResultsPaginator pn, FacetHelper fn, ArtObjectFactory<T> factory, Object... order) throws DataNotReadyException;
	public <T extends ArtObject>List<T> searchArtObjects(SearchHelper<T> searchH, ResultsPaginator pn, FacetHelper fn, ArtObjectFactory<T> factory, FreeTextSearchable<T> freeTextSearcher, Object... order) throws DataNotReadyException;
	public List<ArtObject> 				searchArtObjects(SearchHelper<ArtObject> sh, ResultsPaginator pn, FacetHelper fn, SortHelper<ArtObject> sortH) throws DataNotReadyException;
	public <T extends ArtObject>List<T> searchArtObjects(SearchHelper<T> sh, ResultsPaginator pn, FacetHelper fn, SortHelper<T> sortH, ArtObjectFactory<T> factory) throws DataNotReadyException;
	public <T extends ArtObject>List<T> searchArtObjects(SearchHelper<T> searchH, ResultsPaginator pn, FacetHelper fn, SortHelper<T> sortH, ArtObjectFactory<T> factory, FreeTextSearchable<T> freeTextSearcher) throws DataNotReadyException;
	public <T extends ArtObject>List<T> fetchRelatedWorks(ArtObject baseO, ArtObjectFactory<T> factory) throws DataNotReadyException;
	public List<String> 				suggestArtObjectTitles(String artistName, String titleWords);
	public List<Facet> 					getArtObjectFacetCounts() throws DataNotReadyException;

	// Narrative Services
	public Narrative loadNarrative(long id, String query);
	
	// Constituent Services
	public Constituent 		  				fetchByConstituentID(long cID);
	public <C extends Constituent>C 		fetchByConstituentID(long cID, ConstituentFactory<C> factory);
	public List<Constituent> 				fetchByConstituentIDs(Collection<Long> objectIDs, gov.nga.entities.art.Constituent.SORT... order);
	public <C extends Constituent>List<C> 	fetchByConstituentIDs(Collection<Long> objectIDs, ConstituentFactory<C> factory, gov.nga.entities.art.Constituent.SORT... order);
	public List<Constituent> 				fetchByConstituentIDs(Collection<Long> objectIDs);
	public <C extends Constituent>List<C> 	fetchByConstituentIDs(Collection<Long> objectIDs, ConstituentFactory<C> factory);
	public List<Constituent> 				searchConstituents(SearchHelper<Constituent> sh, ResultsPaginator pn, FacetHelper fn, Object... order);
	public <C extends Constituent>List<C> 	searchConstituents(SearchHelper<C> searchH, ResultsPaginator pn, FacetHelper fn, ConstituentFactory<C> factory, Object... order);
	public <C extends Constituent>List<C> 	searchConstituents(SearchHelper<C> searchH, ResultsPaginator pn, FacetHelper fn, ConstituentFactory<C> factory, FreeTextSearchable<C> freeTextSearcher, Object... order);
	public List<Constituent> 				searchConstituents(SearchHelper<Constituent> sh, ResultsPaginator pn, FacetHelper fn, SortHelper<Constituent> sortH);
	public <C extends Constituent>List<C> 	searchConstituents(SearchHelper<C> sh, ResultsPaginator pn, FacetHelper fn, SortHelper<C> sortH, ConstituentFactory<C> factory);
	public <C extends Constituent>List<C> 	searchConstituents(SearchHelper<C> sh, ResultsPaginator pn, FacetHelper fn, SortHelper<C> sortH, ConstituentFactory<C> factory, FreeTextSearchable<C> freeTextSearcher);
	public Map<String, String> getIndexOfArtistsRanges();
    public List<Suggestion> suggestArtObjectsByArtistName(String baseName);
    public List<Suggestion> suggestArtObjectsByTitle(String baseName);
	public List<String> suggestArtistNames(String base);
	public List<String> suggestOwnerNames(String base);
	public Map<Long, String> suggestOwners(String baseName);
	public Map<String, String> getAllLocationDescriptionsByRoom();

	public Map<Long, ArtObject> getArtObjectsRaw();
	public Map<Long, Constituent> getConstituentsRaw();
    public Map<Long, Location> getLocationsRaw();

	// fetch configuration services reference
	public ConfigService getConfig();

    public Long synchronizationFinishedAt();
}
