/*
Сервлет работает с постами. Может их добовлять, удалять, отправлять. Более подробно ТЗ см.
https://github.com/netology-code/jspr-homeworks/tree/master/04_servlets

Метод init() изменен при помощи DI-java
https://github.com/netology-code/jspr-homeworks/tree/master/05_di
*/

package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;

//    @Override
//    public void init() {
//        // Уровень репозитория
//        final var repository = new PostRepository();
//        // Уровень бизнес-логики
//        final var service = new PostService(repository);
//        // Уровень инфраструктурного кода
//        controller = new PostController(service);
//    }



    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
        final var controller = context.getBean(PostController.class);
//        final var controller = context.getBean("postController");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals("GET") && path.equals("/api/posts")) {
                controller.all(resp);
                return;
            }
            if (method.equals("GET") && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                controller.getById(id, resp);
                return;
            }
            // Если параметр id = 0, то создается новый пост, ему присваеивается id и заисывается ответ - пост с нов. id
            // Если id != 0, то сохраняются изменения в сущ. посте.
            // Если такого поста не существует, то выбрасывается и обрабатывается исключение
            if (method.equals("POST") && path.equals("/api/posts")) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}