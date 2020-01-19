package ninja.goofyahead.main.controller;

import com.fasterxml.jackson.annotation.JsonView;
import ninja.goofyahead.main.annotations.AllowImpersonation;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@WebMvcTest
public class GenerateControllersDoc {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    public void testDocumentation() throws IOException {
        StringBuilder docContent = new StringBuilder();
        File file = new File("documentation/ControllerCheck.adoc");

        addDocumentHeader(docContent);
        addDocumentDescription(docContent);

        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                this.requestMappingHandlerMapping.getHandlerMethods();

        String currentController = "";

        addTablesSection(docContent);

        for (Map.Entry<RequestMappingInfo, HandlerMethod> item : handlerMethods.entrySet()) {
            RequestMappingInfo mapping = item.getKey();
            HandlerMethod method = item.getValue();

            if (!currentController.equals(method.getBean().toString())) {
                if (!currentController.isEmpty()) addCloseTable(docContent);
                currentController = method.getBean().toString();
                addTableHeader(docContent, currentController);
            }
            addTableRow(docContent, mapping, method);
        }

        addCloseTable(docContent);
        addDocumentFooter(docContent);

        FileUtils.writeStringToFile(file, docContent.toString(), "UTF-8");
    }

    private void addTablesSection(StringBuilder docContent) {
        docContent.append("== Tables \n \n");
    }

    private void addDocumentFooter(StringBuilder docContent) {
        docContent.append("== Links \n \n");
        docContent.append("* https://www.baeldung.com/spring-mvc-handlerinterceptor[Handler Interceptor Doc] \n");
        docContent.append("* https://www.baeldung.com/jackson-json-view-annotation[Jackson View Doc]\n");
    }

    private void addCloseTable(StringBuilder docContent) {
        docContent.append("|=== \n \n");
    }

    private void addTableRow(StringBuilder docContent, RequestMappingInfo mapping, HandlerMethod method) {
        docContent.append("|")
                .append(mapping.getPatternsCondition().toString())
                .append("|")
                .append(mapping.getMethodsCondition().toString())
                .append("|")
                .append(convertToIcon(method.getMethodParameters()[0].hasParameterAnnotation(RequestBody.class)))
                .append("|")
                .append(convertToIcon(method.hasMethodAnnotation(AllowImpersonation.class)))
                .append("|")
                .append(convertToIcon(method.getMethodParameters()[0].hasParameterAnnotation(JsonView.class)))
                .append("\n");
    }

    private void addTableHeader(StringBuilder docContent, String currentController) {
        docContent.append("=== ").append(currentController).append("\n \n");
        docContent.append(".Table ").append(currentController).append(" controller table: \n");
        docContent.append("|=== \n");
        docContent.append("| Url | Method | Takes object | Impersonation | Is filtered \n");
    }

    private void addDocumentDescription(StringBuilder docContent) {
        docContent.append("== Purpose: \n \n");
        docContent.append("This table should be used to check that all the controllers follow the secured approach " +
                "anything marked as not filtered should be checked. It iterates through all the controllers and checks their annotations \n \n");

        docContent.append("The proposed solution is to mark the input objects with `@JsonView(View.Public.class)` " +
                "link:{sourcedir}/ninja/goofyahead/main/annotations/AllowImpersonation.java[Definition] on any field that should " +
                "not be accepted from the front-end and should be picked up from the security context"+
                "because mawani accepts impersonation on some endpoints this should be added as an annotation to handle this transparently " +
                "`@AllowImpersonation` at method level, therefore the check mark for AllowsImpersonation in the table. \n \n");

        docContent.append("=== Impersonation annotation \n");
        docContent.append("[source,java]\n" +
                "----\n" +
                "include::{sourcedir}/ninja/goofyahead/main/annotations/AllowImpersonation.java[]\n" +
                "----\n \n");

        docContent.append("=== Impersonation implementation \n");
        docContent.append("[source,java]\n" +
                "----\n" +
                "include::{sourcedir}/ninja/goofyahead/main/interceptor/ImpersonateInterceptor.java[]\n" +
                "----\n \n");

        docContent.append("=== Views annotation \n");
        docContent.append("[source,java]\n" +
                "----\n" +
                "include::{sourcedir}/ninja/goofyahead/main/views/View.java[]\n" +
                "----\n \n");
    }

    private void addDocumentHeader(StringBuilder docContent) {
        docContent.append(":icons: font \n \n");
        docContent.append(":sourcedir: ../src/main/java \n \n");

        docContent.append("= Controller doc: \n ");
        docContent.append("Autogenerated: v1.0, ").append(new Date().toString()).append("\n");
        docContent.append(":toc:").append(" \n \n");
    }

    private String convertToIcon(boolean result) {
        if (result) {
            return "icon:check[role=\"green\"]";
        } else {
            return "icon:exclamation-circle[role=\"red\"]";
        }
    }

}