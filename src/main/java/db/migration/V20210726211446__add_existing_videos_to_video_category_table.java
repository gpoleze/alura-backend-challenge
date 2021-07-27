package db.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V20210726211446__add_existing_videos_to_video_category_table extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            UUID generalCategoryId;

            try (ResultSet rows = select.executeQuery("SELECT id FROM category WHERE title = 'GENERAL'")) {
                rows.next();
                generalCategoryId = UUID.fromString(rows.getString("id"));
            }

            try (ResultSet rows = select.executeQuery("SELECT id FROM video")) {
                while (rows.next()) {
                    UUID videoId = UUID.fromString(rows.getString("id"));
                    String sql = "INSERT INTO video_category(id,video_id, category_id) VALUES (?,?,?)";

                    try (PreparedStatement prepareStatement = context.getConnection().prepareStatement(sql)) {
                        prepareStatement.setObject(1, UUID.randomUUID(), Types.OTHER);
                        prepareStatement.setObject(2, videoId, Types.OTHER);
                        prepareStatement.setObject(3, generalCategoryId, Types.OTHER);

                        prepareStatement.execute();
                    }
                }
            }
        }
    }
}