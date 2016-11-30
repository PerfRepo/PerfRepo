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

ALTER TABLE value DROP CONSTRAINT fk6ac9171c4825995;
ALTER TABLE ONLY value ADD CONSTRAINT fk6ac9171c4825995 FOREIGN KEY (metric_id) REFERENCES metric(id) ON DELETE CASCADE;

ALTER TABLE value DROP CONSTRAINT fk6ac9171fdfcba9a;
ALTER TABLE ONLY value ADD CONSTRAINT fk6ac9171fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id) ON DELETE CASCADE;

ALTER TABLE test_execution_parameter DROP CONSTRAINT fk8f4e9015fdfcba9a;
ALTER TABLE ONLY test_execution_parameter ADD CONSTRAINT fk8f4e9015fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id) ON DELETE CASCADE;

ALTER TABLE test_execution DROP CONSTRAINT fkc4d93bab3f0c1115;
ALTER TABLE ONLY test_execution ADD CONSTRAINT fkc4d93bab3f0c1115 FOREIGN KEY (test_id) REFERENCES test(id) ON DELETE CASCADE;

ALTER TABLE test_execution_attachment DROP CONSTRAINT fkca230a37fdfcba9a;
ALTER TABLE ONLY test_execution_attachment ADD CONSTRAINT fkca230a37fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id) ON DELETE CASCADE;

ALTER TABLE public.report DROP CONSTRAINT report_user_fkey;
ALTER TABLE ONLY public.report ADD CONSTRAINT report_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;

ALTER TABLE public.report_property DROP CONSTRAINT report_property_report_fkey;
ALTER TABLE ONLY public.report_property ADD CONSTRAINT report_property_report_fkey FOREIGN KEY (report_id) REFERENCES "report"(id) ON DELETE CASCADE;

ALTER TABLE public.permission DROP CONSTRAINT permission_report_fkey;
ALTER TABLE ONLY public.permission ADD CONSTRAINT permission_report_fkey FOREIGN KEY(report_id) REFERENCES "report"(id) ON DELETE CASCADE;

ALTER TABLE public.permission DROP CONSTRAINT permission_group_fkey;
ALTER TABLE ONLY public.permission ADD CONSTRAINT permission_group_fkey FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE;

ALTER TABLE public.permission DROP CONSTRAINT permission_user_fkey;
ALTER TABLE ONLY public.permission ADD CONSTRAINT permission_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;

ALTER TABLE public.alert DROP CONSTRAINT alert_metric_fkey;
ALTER TABLE ONLY public.alert ADD CONSTRAINT alert_metric_fkey FOREIGN KEY (metric_id) REFERENCES metric(id) ON DELETE CASCADE;

ALTER TABLE public.alert DROP CONSTRAINT alert_test_fkey;
ALTER TABLE ONLY public.alert ADD CONSTRAINT alert_test_fkey FOREIGN KEY (test_id) REFERENCES test(id) ON DELETE CASCADE;

ALTER TABLE public.alert_tag DROP CONSTRAINT alert_tag_alert_fkey;
ALTER TABLE ONLY public.alert_tag ADD CONSTRAINT alert_tag_alert_fkey FOREIGN KEY (alert_id) REFERENCES alert(id) ON DELETE CASCADE;

ALTER TABLE public.alert_tag DROP CONSTRAINT alert_tag_tag_fkey;
ALTER TABLE ONLY public.alert_tag ADD CONSTRAINT alert_tag_tag_fkey FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE;

COMMIT;

BEGIN;

ALTER TABLE public.user ADD COLUMN type character varying(25) NOT NULL;

ALTER TABLE user_group ADD COLUMN type character varying(25) NOT NULL;

COMMIT;
