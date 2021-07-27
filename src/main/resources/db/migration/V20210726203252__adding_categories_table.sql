ALTER TABLE video
    ALTER COLUMN id
    SET DEFAULT uuid_generate_v4();

CREATE TABLE category (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR NOT NULL,
    color VARCHAR NOT NULL
);

CREATE TABLE video_category (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    video_id UUID NOT NULL,
    category_id UUID NOT NULL,
    CONSTRAINT fk_video_id FOREIGN KEY(video_id) REFERENCES video(id),
    CONSTRAINT fk_category_id FOREIGN KEY(category_id) REFERENCES category(id)
);

INSERT INTO category("id","title","color") values (uuid_generate_v4(), 'GENERAL','000000');