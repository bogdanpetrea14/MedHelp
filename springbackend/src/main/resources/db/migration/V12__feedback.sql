CREATE TABLE feedback (
                          id              uuid                NOT NULL,
                          user_id         uuid                NOT NULL,
                          category        feedback_category   NOT NULL,
                          rating          feedback_rating     NOT NULL,
                          allow_contact   boolean             NOT NULL DEFAULT FALSE,
                          details         text                NOT NULL,
                          created_at      timestamp           NOT NULL DEFAULT NOW(),
                          PRIMARY KEY (id),
                          FOREIGN KEY (user_id) REFERENCES medconnect.users (id) ON DELETE CASCADE
);