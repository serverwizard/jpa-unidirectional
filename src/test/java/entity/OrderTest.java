package entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.ddl.DdlGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private final DdlGenerator ddlGenerator = new DdlGenerator();

    @Test
    @DisplayName("@ManyToOne 단방향 + @JoinColumn으로 users 테이블 DDL을 생성한다")
    void usersCreateTableDdl() {
        final String ddl = ddlGenerator.generateCreateTable(User.class);

        assertThat(ddl).isEqualTo(
                "CREATE TABLE users (\n" +
                        "    id BIGINT PRIMARY KEY,\n" +
                        "    name VARCHAR(255)\n" +
                        ");"
        );
    }

    @Test
    @DisplayName("@ManyToOne 단방향 + @JoinColumn으로 orders 테이블 DDL을 생성한다")
    void ordersCreateTableDdl() {
        final String ddl = ddlGenerator.generateCreateTable(Order.class);

        assertThat(ddl).isEqualTo(
                "CREATE TABLE orders (\n" +
                        "    id BIGINT PRIMARY KEY,\n" +
                        "    product_name VARCHAR(255),\n" +
                        "    user_id BIGINT,\n" +
                        "    FOREIGN KEY (user_id) REFERENCES users(id)\n" +
                        ");"
        );
    }
}
