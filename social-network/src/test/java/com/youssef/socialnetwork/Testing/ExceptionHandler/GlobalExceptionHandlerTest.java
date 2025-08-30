package com.youssef.socialnetwork.Testing.ExceptionHandler;

import com.youssef.socialnetwork.Exceptions.AccessDeniedException;
import com.youssef.socialnetwork.Exceptions.GlobalExceptionHandler;
import com.youssef.socialnetwork.Exceptions.PostNotFoundException;
import com.youssef.socialnetwork.Exceptions.UserNotFoundException;
import com.youssef.socialnetwork.dto.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void testHandleUserNotFound() {
        UserNotFoundException ex = new UserNotFoundException(99L);

        ResponseEntity<ApiError> response = handler.handleUserNotFound(ex, webRequest);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("User Not Found", response.getBody().getError());
        assertEquals("User with ID 99 not found", response.getBody().getMessage());
        assertTrue(response.getBody().getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void testHandlePostNotFound() {
        PostNotFoundException ex = new PostNotFoundException(123L);

        ResponseEntity<ApiError> response = handler.handlePostNotFound(ex, webRequest);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Post Not Found", response.getBody().getError());
        assertEquals("Post with ID 123 not found", response.getBody().getMessage());
    }

    @Test
    void testHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException();

        ResponseEntity<ApiError> response = handler.handleAccessDenied(ex, webRequest);

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Access Denied", response.getBody().getError());
        assertEquals("You do not have permission to access this resource", response.getBody().getMessage());
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ApiError> response = handler.handleGeneralException(ex, webRequest);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Something went wrong", response.getBody().getMessage());
    }
}
