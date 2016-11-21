-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 1.7 to 2.0                                           --
--                                                                                           --
-----------------------------------------------------------------------------------------------
BEGIN;

CREATE FUNCTION migrate_test_group_relations()
 RETURNS void AS
$$
DECLARE
    row test;
    selectedGroupId bigint;
BEGIN
    FOR row IN (SELECT * FROM test)
    LOOP
        EXECUTE 'SELECT DISTINCT id FROM public.group WHERE name = $1' INTO selectedGroupId USING row.groupid;
        EXECUTE 'UPDATE test SET group_id = $1 WHERE id = $2' USING selectedGroupId, row.id;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

COMMIT;

BEGIN;

ALTER TABLE test ADD COLUMN group_id bigint;
ALTER TABLE ONLY test ADD CONSTRAINT test_group_fk FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE;

SELECT migrate_test_group_relations();

ALTER TABLE test ALTER COLUMN group_id SET NOT NULL;
CREATE INDEX test_group_id ON test(group_id);
ALTER TABLE test DROP COLUMN groupid;

COMMIT;

BEGIN;

ALTER TABLE favorite_parameter DROP CONSTRAINT favorite_parameter_user_fkey;
ALTER TABLE ONLY favorite_parameter ADD CONSTRAINT favorite_parameter_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;
ALTER TABLE favorite_parameter DROP CONSTRAINT favorite_parameter_test_fkey;
ALTER TABLE ONLY favorite_parameter ADD CONSTRAINT favorite_parameter_test_fkey FOREIGN KEY (test_id) REFERENCES "test"(id) ON DELETE CASCADE;

COMMIT;
