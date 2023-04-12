package es.codeurjc.backend.utilities.responseentity;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.codeurjc.backend.utilities.PageableUtil;
import es.codeurjc.backend.utilities.queriers.CollectionQueriable;
import es.codeurjc.backend.utilities.queriers.IndividualQueriable;


// 13-A
public class GetResponseEntityGenerator <T, U> {

    private IndividualQueriable<T, ?> pathVariableSubjectQueriable;

    private IndividualQueriable<U, ?> paramSubjectQueriable;
    
    public GetResponseEntityGenerator(IndividualQueriable<T, ?> pathVariableSubjectQueriable, IndividualQueriable<U, ?> paramSubjectQueriable) 
    {
        this.pathVariableSubjectQueriable = pathVariableSubjectQueriable;
        this.paramSubjectQueriable = paramSubjectQueriable;
    }

    public ResponseEntity<Object> generateResponseEntity(
        String subjectId, int page, int size, Optional<String> searchedId,
        CollectionQueriable<T, U, ?> checkedCollectionQuerier)
    {
        return generateGetResponseEntity(
            subjectId, page, size, Optional.empty(), 
            null, searchedId, checkedCollectionQuerier);
    }

    public ResponseEntity<Object> generateGetResponseEntity(
        String subjectId, int page, int size, Optional<String> sortBy, String direction,
        Optional<String> searchedId, CollectionQueriable<T, U, ?> checkedCollectionQuerier)
    {
        try
        {    
            Optional<T> pathVariableSubjectOpt = pathVariableSubjectQueriable.doQuery(subjectId);
            if (pathVariableSubjectOpt.isPresent()) 
            {
                if (searchedId.isEmpty()) 
                {
                    Pageable pageable;
                    
                    if (sortBy.isEmpty())
                        pageable = PageRequest.of(page, size);
                    else
                        pageable = PageableUtil.getPageable(page, size, sortBy.get(), direction);
                    
                    Collection<U> collection = checkedCollectionQuerier.doQuery(pathVariableSubjectOpt, pageable);
                    return new ResponseEntity<>(collection, HttpStatus.OK);  
                } 
                else 
                {
                    Collection<U> checkedCollection = 
                    checkedCollectionQuerier.doQuery(pathVariableSubjectOpt, Pageable.unpaged());

                    Optional<U> paramSubject = paramSubjectQueriable.doQuery(searchedId.get());      
                    Boolean inCollection;

                    if (paramSubject.isPresent())
                        inCollection = checkedCollection.contains(paramSubject.get());
                    else
                        inCollection = false;

                    return new ResponseEntity<>(inCollection, HttpStatus.OK);
                }
            }
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (NumberFormatException e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
