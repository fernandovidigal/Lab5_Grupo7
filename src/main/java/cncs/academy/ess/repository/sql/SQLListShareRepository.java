package cncs.academy.ess.repository.sql;

import cncs.academy.ess.model.ListShare;
import cncs.academy.ess.repository.ListShareRepository;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLListShareRepository implements ListShareRepository {
    private final BasicDataSource dataSource;

    public SQLListShareRepository(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public void share(int listId, int userId){
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO list_shares (list_id, user_id) VALUES (?,?)");
            stmt.setInt(1, listId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to share list", e);
        }
    }

    @Override
    public void revoke(int listId, int userId){
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM list_shares WHERE list_id = ? AND user_id = ?");
            stmt.setInt(1, listId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to revoke share", e);
        }
    }

    @Override
    public List<ListShare> findSharesByListId(int listId){
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM list_shares WHERE list_id = ?");
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            List<ListShare> shares = new ArrayList<>();
            while(rs.next()){
                shares.add(mapResultSetToListShare(rs));
            }
            return shares;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find shares by list", e);
        }
    }

    @Override
    public List<Integer> findSharedListIdsByUserId(int userId){
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("SELECT list_id FROM list_shares WHERE user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            List<Integer> listIds = new ArrayList<>();
            while(rs.next()){
                listIds.add(rs.getInt("list_id"));
            }
            return listIds;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find shared lists by user ID", e);
        }
    }

    @Override
    public ListShare findShare(int listId, int userId){
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("SELECT * list_shares WHERE list_id = ? AND user_id = ?");
            stmt.setInt(1, listId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return mapResultSetToListShare(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find share", e);
        }
        return null;
    }

    private ListShare mapResultSetToListShare(ResultSet rs) throws SQLException {
        int listId = rs.getInt("list_id");
        int userId = rs.getInt("user_id");
        String permission = rs.getString("permission");
        return new ListShare(listId, userId, permission);
    }
}
