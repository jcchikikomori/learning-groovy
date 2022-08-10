package example

import static org.springframework.http.HttpStatus.*

import grails.validation.ValidationException

/**
 * This is a controller class. It is responsible for handling requests and returning responses.
 */
class PostController {

    PostService postService

    static postLabelCode = 'post.label'
    static postLabelDefault = 'Post'

    static allowedMethods = [save: 'POST', update: 'PUT', delete: 'DELETE']

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond postService.list(params), model: [postCount: postService.count()]
    }

    def show(Long id) {
        respond postService.get(id)
    }

    def create() {
        respond new Post(params)
    }

    def save(Post post) {
        if (post == null) {
            notFound()
            return
        }

        try {
            postService.save(post)
        } catch (ValidationException e) {
            respond post.errors, view: 'create'
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(
                    code: 'default.created.message',
                    args: [message(code: postLabelCode, default: postLabelDefault), post.id])
                redirect post
            }
            '*' { respond post, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond postService.get(id)
    }

    def update(Post post) {
        if (post == null) {
            notFound()
            return
        }

        try {
            postService.save(post)
        } catch (ValidationException e) {
            respond post.errors, view: 'edit'
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(
                    code: 'default.updated.message',
                    args: [message(code: postLabelCode, default: postLabelDefault), post.id])
                redirect post
            }
            '*' { respond post, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        postService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(
                    code: 'default.deleted.message',
                    args: [message(code: postLabelCode, default: postLabelDefault), id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(
                    code: 'default.not.found.message',
                    args: [message(code: postLabelCode, default: postLabelDefault), params.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NOT_FOUND }
        }
    }

}
