package es.codeurjc.backend.utilities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// 13-A
public class PageableUtil {

    public static Pageable getPageable (int page, int size, String sortBy, String direction) 
    {
        return PageRequest.of(page, size, PageableUtil.getSort(sortBy, direction));
    }
    
    public static Sort getSort (String sortBy, String direction)
    {
        String[] sortByArray = sortBy.split("\\s+");
        String[] directionsArray = direction.split("\\s+");

        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortByArray.length; i++) 
        {
            Sort.Order order;
            if (i < directionsArray.length && "desc".equalsIgnoreCase(directionsArray[i]))
                order = Sort.Order.desc(sortByArray[i]);
            else 
                order = Sort.Order.asc(sortByArray[i]);
            
            orders.add(order);
        }
        return Sort.by(orders);
    }  
}
