package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.net.http.HttpClient;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    //метод отправляет ответ со списком всех постов
    public void all(HttpServletResponse response) throws IOException {
        oK200(new Gson(), service.all(), response);
    }

    public void getById(long id, HttpServletResponse response) throws IOException {
        final var gson = new Gson();
        try {
            final var data = service.getById(id);
            oK200(gson, data, response);
        } catch (NotFoundException e) {
            notFound404(e, response);
        }
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        try {
            final var gson = new Gson();
            final var data = service.save(gson.fromJson(body, Post.class));
            oK200(gson, data, response);
        } catch (NotFoundException e) {
            notFound404(e, response);
        }
    }

    public void removeById(long id, HttpServletResponse response) throws IOException {
        try {
            service.removeById(id);
            response.setStatus(200);
            response.getWriter().print("Post (id: " + id + ") has been deleted");
        } catch (NotFoundException e) {
            notFound404(e, response);
        }
    }

    void notFound404(NotFoundException e, HttpServletResponse response) throws IOException {
        response.setStatus(404);
        response.getWriter().print(e.getMessage());
    }

    <T> void oK200(Gson gson, T data, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().print(gson.toJson(data));
    }
}
