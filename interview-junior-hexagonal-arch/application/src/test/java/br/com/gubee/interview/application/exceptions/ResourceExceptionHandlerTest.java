package br.com.gubee.interview.application.exceptions;

import br.com.gubee.interview.application.hero.exception.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class ResourceExceptionHandlerTest {

    ResourceExceptionHandler resourceExceptionHandler = new ResourceExceptionHandler();


    @Test
    void objectNotFound() {
        //arrange
        var exception = new ObjectNotFoundException("test");
        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/test-uri");

        //act
        var response = resourceExceptionHandler.objectNotFound(exception, request);
        //assert
        assertEquals(exception.getMessage(), response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Object not found", response.getBody().getError());
        assertEquals(request.getRequestURI(), response.getBody().getPath());
    }
}