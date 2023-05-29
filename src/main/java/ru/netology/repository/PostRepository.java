package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
    private ConcurrentHashMap<Long, Post> postRepository = new ConcurrentHashMap<>();
    private AtomicLong lastId = new AtomicLong(0);

    public List<Post> all() {
        return postRepository.values().stream().sorted(Comparator.comparingLong(Post::getId)).toList();
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(postRepository.get(id));
    }

    public Post save(Post post) throws NotFoundException {
        if (post.getId() == 0) {
            post.setId(lastId.incrementAndGet());
        } else if (postRepository.get(post.getId()) == null) {
            throw new NotFoundException("Post not found by ID: " + post.getId());
        }
        postRepository.put(post.getId(), post);
        return post;
    }

    public void removeById(long id) {
        if (!postRepository.containsKey(id)) {
            throw new NotFoundException("Post not found by ID: " + id);
        }
        postRepository.remove(id);
    }
}