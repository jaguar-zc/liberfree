package ${dao};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;


@Repository
public class ${name}QueryRepository   {

    @Autowired
    private EntityManager em;


    public List<${name}> list(Pagination page){
        StringBuffer sql = new StringBuffer("FROM ${name} WHERE 1=1 ");
        Query query = em.createQuery(sql.toString(),${name}.class);
        query.setFirstResult(page.getStart());
        query.setMaxResults(page.getPageSize());
        List resultList = query.getResultList();
        return resultList;
    }

    public Long count(){
        StringBuffer sql = new StringBuffer("SELECT COUNT(1) FROM ${name} ");
        Query query = em.createQuery(sql.toString(),Long.class);
        Object singleResult = query.getSingleResult();
        return  (Long) singleResult;
    }


}
