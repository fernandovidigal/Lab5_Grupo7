package cncs.academy.ess.repository;

import cncs.academy.ess.model.ListShare;

import java.util.List;

public interface ListShareRepository {
    void share(int listId, int userId);
    void revoke(int listId, int userId);
    List<ListShare> findSharesByListId(int listId);
    List<Integer> findSharedListIdsByUserId(int userId);
    ListShare findShare(int listId, int userId);
}
