package org.perfrepo.web.service;

import org.perfrepo.model.Tag;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionAttachment;
import org.perfrepo.model.TestExecutionParameter;
import org.perfrepo.model.Value;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.dao.TagDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionAttachmentDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.dao.TestExecutionParameterDAO;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.util.MultiValue;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestExecutionServiceBean implements TestExecutionService {

    @Inject
    private TestDAO testDAO;

    @Inject
    private TestExecutionDAO testExecutionDAO;

    @Inject
    private TestExecutionParameterDAO testExecutionParameterDAO;

    @Inject
    private TestExecutionAttachmentDAO testExecutionAttachmentDAO;

    @Inject
    private AlertingService alertingService;

    @Inject
    private TagDAO tagDAO;

    @Override
    public TestExecution createTestExecution(TestExecution testExecution) throws UnauthorizedException {
        Test managedTest = testDAO.get(testExecution.getTest().getId());
        testExecution.setTest(managedTest);

        Set<Tag> tags = testExecution.getTags();
        for (Tag tag: tags) {
            addTag(tag, testExecution);
        }


        return testExecutionDAO.create(testExecution);
        /*Collection<Tag> detachedTags = testExecution.getTags();
        testExecution.setTags(new HashSet<>());
        TestExecution storedTestExecution = testExecutionDAO.create(testExecution);
        // execution params
        if (testExecution.getParameters() != null && testExecution.getParameters().size() > 0) {
            for (String paramKey : testExecution.getParameters().keySet()) {
                TestExecutionParameter parameter = testExecution.getParameters().get(paramKey);
                parameter.setTestExecution(storedTestExecution);
                testExecutionParameterDAO.create(parameter);
            }
        }
        // tags
        if (detachedTags != null && detachedTags.size() > 0) {
            for (Tag teg : detachedTags) {
                Tag tag = tagDAO.findByName(teg.getName());
                if (tag == null) {
                    tag = tagDAO.create(teg);
                }

                storedTestExecution.getTags().add(tag);
            }
        }
        // values
        //TODO: solve this
      /*
      if (testExecution.getValues() != null && !testExecution.getValues().isEmpty()) {
         for (Value value : testExecution.getValues()) {
            value.setTestExecution(storedTestExecution);
            if (value.getMetricName() == null) {
               throw new IllegalArgumentException("Metric name is mandatory");
            }
            Metric metric = test.getMetrics().stream().filter(m -> m.getName().equals(value.getMetricName())).findFirst().get();
            if (metric == null) {
               throw new ServiceException("serviceException.metricNotInTest", test.getName(), test.getId().toString(), value.getMetricName());
            }
            value.setMetric(metric);
            valueDAO.create(value);
            if (value.getParameters() != null && value.getParameters().size() > 0) {
               for (ValueParameter vp : value.getParameters()) {
                  vp.setValue(value);
                  valueParameterDAO.create(vp);
               }
            }
         }
      }*/
        /*
        storedTestExecution = testExecutionDAO.merge(storedTestExecution);

        TestExecution clone = storedTestExecution;

        alertingService.processAlerts(clone);
        */
    }

    @Override
    public TestExecution updateTestExecution(TestExecution updatedTestExecution) throws UnauthorizedException {
        TestExecution managedTestExecution = testExecutionDAO.merge(updatedTestExecution);

        return managedTestExecution;
    }

    @Override
    public void removeTestExecution(TestExecution testExecution) throws UnauthorizedException {
        TestExecution freshTestExecution = testExecutionDAO.get(testExecution.getId());
        /*if (freshTestExecution == null) {
            throw new ServiceException("serviceException.testExecutionNotFound", testExecution.getName());
        }*/
        //TODO: solve this
      /*
      for (TestExecutionParameter testExecutionParameter : freshTestExecution.getParameters()) {
         testExecutionParameterDAO.remove(testExecutionParameter);
      }*/
        //TODO: solve this
      /*
      for (Value value : freshTestExecution.getValues()) {
         for (ValueParameter valueParameter : value.getParameters()) {
            valueParameterDAO.remove(valueParameter);
         }
         valueDAO.remove(value);
      }*/

        //TODO: solve this
      /*
      Iterator<TestExecutionAttachment> allTestExecutionAttachments = freshTestExecution.getAttachments().iterator();
      while (allTestExecutionAttachments.hasNext()) {
         testExecutionAttachmentDAO.remove(allTestExecutionAttachments.next());
         allTestExecutionAttachments.remove();
      }
      */
        testExecutionDAO.remove(freshTestExecution);
    }

    @Override
    public TestExecution getTestExecution(Long id) {
        return null;
    }

    @Override
    public List<TestExecution> getAllTestExecutions() {
        return null;
    }

    @Override
    public SearchResultWrapper<TestExecution> searchTestExecutions(TestExecutionSearchTO search) {
        // remove param criteria with empty param name
        if (search.getParameters() != null) {
            for (Iterator<TestExecutionSearchTO.ParamCriteria> allParams = search.getParameters().iterator(); allParams.hasNext();) {
                TestExecutionSearchTO.ParamCriteria param = allParams.next();
                if (param.isNameEmpty()) {
                    allParams.remove();
                }
            }
        }

        //TODO: solve this
        //return testExecutionDAO.searchTestExecutions(search, userService.getLoggedUserGroupNames());
        return null;
    }

    @Override
    public Long addAttachment(TestExecutionAttachment attachment) throws UnauthorizedException {
        TestExecution exec = testExecutionDAO.get(attachment.getTestExecution().getId());
        /*if (exec == null) {
            throw new ServiceException("serviceException.addAttachment.testExecutionNotFound", attachment.getTestExecution().getName());
        }*/
        attachment.setTestExecution(exec);
        TestExecutionAttachment newAttachment = testExecutionAttachmentDAO.create(attachment);
        return newAttachment.getId();
    }

    @Override
    public void removeAttachment(TestExecutionAttachment attachment) throws UnauthorizedException {
        TestExecution exec = testExecutionDAO.get(attachment.getTestExecution().getId());
        /*if (exec == null) {
            throw new ServiceException("serviceException.removeAttachment.testExecutionNotFound", attachment.getTestExecution().getName());
        }*/
        TestExecutionAttachment freshAttachment = testExecutionAttachmentDAO.get(attachment.getId());
        if (freshAttachment != null) {
            testExecutionAttachmentDAO.remove(freshAttachment);
        }
    }

    @Override
    public TestExecutionAttachment getAttachment(Long id) {
        return testExecutionAttachmentDAO.get(id);
    }

    @Override
    public TestExecutionParameter addParameter(TestExecutionParameter parameter) throws UnauthorizedException {
        return null;
    }

    @Override
    public TestExecutionParameter updateParameter(TestExecutionParameter parameter) throws UnauthorizedException {
        return null;
    }

    @Override
    public void removeParameter(TestExecutionParameter parameter) throws UnauthorizedException {

    }

    @Override
    public TestExecutionParameter getParameter(Long id) {
        return null;
    }

    @Override
    public List<TestExecutionParameter> getParametersByPrefix(String prefix) {
        return null;
    }

    @Override
    public Value addValue(Value value) throws UnauthorizedException {
        return null;
    }

    @Override
    public Value updateValue(Value value) throws UnauthorizedException {
        return null;
    }

    @Override
    public void removeValue(Value value) throws UnauthorizedException {

    }

    @Override
    public Tag addTag(Tag tag, TestExecution testExecution) throws UnauthorizedException {
        return null;
    }

    @Override
    public Tag updateTag(Tag tag) throws UnauthorizedException {
        return null;
    }

    @Override
    public void removeTag(Tag tag) throws UnauthorizedException {

    }

    @Override
    public List<String> getTagsByPrefix(String prefix) {
        return null;
    }

    @Override
    public void addTagsToTestExecutions(Set<Tag> tags, Collection<TestExecution> testExecutions) {

    }

    @Override
    public void removeTagsFromTestExecutions(Set<Tag> tags, Collection<TestExecution> testExecutions) {

    }

    /**** HELPER METHODS ****/

    /**
     * Validates correct format of test execution.
     *
     * @param testExecution
     * @throws ServiceException
     */
    private void validateTestExecution(TestExecution testExecution) throws ServiceException {
        try {
            boolean isMultivalue = MultiValue.isMultivalue(testExecution);
        } catch (IllegalStateException ex) {
            throw new ServiceException("page.exec.invalidMultiValue", testExecution.getName());
        }
    }
}
