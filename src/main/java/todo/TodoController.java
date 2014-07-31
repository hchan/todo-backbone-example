package todo;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.resthub.common.util.PostInitialize;
import org.resthub.web.controller.RepositoryBasedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
/**
 * This Spring MVC controller extends RESThub REST controller for CRUD operations, and show how to extend it with your
 * application specific functionnalites.
 */
@Controller
//@RequestMapping(value = "/api/todo2", produces="application/json")
@RequestMapping(value = "/api/todo")
public class TodoController extends RepositoryBasedRestController<Todo, Long, TodoRepository> {

    protected Logger logger = LoggerFactory.getLogger(TodoController.class);
	private EntityManagerFactory emf;

    @PostInitialize
    public void init() {
        this.repository.save(new Todo("test123"));
    }

    @Inject
    @Override
    public void setRepository(TodoRepository repository) {
        this.repository = repository;
    }
    
    @Inject
    public void setEntityManagerFactory(EntityManagerFactory emf) {
    	this.emf = emf;
    }
    
    @RequestMapping(value = "greaterThan/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Page<Todo> searchByGreaterThanId(@PathVariable Integer id) {
    	Page<Todo> retval = null;
       EntityManager em = null;
       try {
    	   em = emf.createEntityManager();
    	   retval = new PageImpl<Todo>(em.createNativeQuery("SELECT * FROM TODO WHERE ID > :id", Todo.class).setParameter("id", id).getResultList(),
    			   new PageRequest(1, 100), 100);
    	   System.out.println(retval);
    	   System.out.println("---");
    	   System.out.println(this.repository.findByContentLike("%" + "e" + "%"));
       } catch (Exception e) {
    	   e.printStackTrace();
       } finally {
    	   if (em != null) {
    		   em.close();
    	   }
       }
       return retval;
    }

    @RequestMapping(value = "content/{content}", method = RequestMethod.GET)
    @ResponseBody
    public Page<Todo> searchByContent(@PathVariable String content) {
    	Page<Todo> retval = new PageImpl<Todo>(this.repository.findByContentLike("%" + content + "%"), new PageRequest(1, 100), 100);
        return retval;
    }
}
