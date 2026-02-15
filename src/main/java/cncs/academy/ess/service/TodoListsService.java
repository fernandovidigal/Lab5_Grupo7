package cncs.academy.ess.service;

import cncs.academy.ess.model.ListShare;
import cncs.academy.ess.model.TodoList;
import cncs.academy.ess.repository.ListShareRepository;
import cncs.academy.ess.repository.TodoListsRepository;

import java.util.Collection;
import java.util.List;

public class TodoListsService {
    TodoListsRepository todoListsRepository;
    ListShareRepository listShareRepository;

    public TodoListsService(TodoListsRepository todoListsRepository, ListShareRepository listShareRepository) {
        this.todoListsRepository = todoListsRepository;
        this.listShareRepository = listShareRepository;
    }

    public TodoList createTodoListItem(String listName, int ownerId) {
        TodoList list = new TodoList(listName, ownerId);
        int listId = todoListsRepository.save(list);
        list.setId(listId);
        return list;
    }
    public TodoList getTodoList(int listId) {
        return todoListsRepository.findById(listId);
    }

    // Devolve a lista apenas se o utilizador tive pelo menos permissão de leitura
    // Caso contrário devolve null
    public TodoList getTodoListForUser(int listId, int userId){
        TodoList list = todoListsRepository.findById(listId);
        if(list == null){
            return null;
        }

        // Dono da list tem sempre acesso
        if(list.getOwnerId() == userId){
            return list;
        }

        // Verificar se a lista foi partilhada com este utilizador
        ListShare share = listShareRepository.findShare(listId, userId);
        if(share != null){
            return list;
        }

        // Não tem acesso
        return null;
    }

    public Collection<TodoList> getAllTodoLists(int userId) {
        // Listas do próprio utilizador
        List<TodoList> ownedLists = todoListsRepository.findAllByUserId(userId);

        // Listas partilhadas com este utilizador
        List<Integer> sharedListsIds  = listShareRepository.findSharedListIdsByUserId(userId);
        for(Integer listId : sharedListsIds){
            TodoList list = todoListsRepository.findById(listId);
            if(list != null && list.getOwnerId() != userId){
                ownedLists.add(list);
            }
        }
        return ownedLists;
    }

    // Partilhar uma lista com outro utilizador. Apenas o dono pode partilhar
    public void shareTodoList(int listId, int ownerId, int targetUserId) {
        TodoList list = todoListsRepository.findById(listId);
        if(list == null){
            throw new IllegalArgumentException("List not found");
        }
        if(list.getOwnerId() != ownerId){
            throw new SecurityException("Only owner can share this list");
        }
        if(list.getOwnerId() == targetUserId){
            throw new IllegalArgumentException("You are the owner of the list");
        }
        listShareRepository.share(listId, targetUserId);
    }

    // Revogar partilha de uma lista. Apenas o dono pode revogar
    public void revokeTodoListShare(int listId, int ownerId, int targetUserId){
        TodoList list = todoListsRepository.findById(listId);
        if(list == null){
            throw new IllegalArgumentException("List not found");
        }
        if(list.getOwnerId() != ownerId){
            throw new SecurityException("Only owner can revoke sharing for this list");
        }
        listShareRepository.revoke(listId, targetUserId);
    }
}
