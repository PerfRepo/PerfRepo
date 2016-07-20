-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 1.4 to 1.5                                           --
--                                                                                           --
-----------------------------------------------------------------------------------------------

BEGIN;


ALTER TABLE test ALTER COLUMN description DROP NOT NULL;
ALTER TABLE metric ALTER COLUMN description DROP NOT NULL;

ALTER TABLE test_execution_tag DROP COLUMN id;
DROP sequence test_execution_tag_sequence;

ALTER TABLE test_metric DROP COLUMN id;
DROP sequence test_metric_sequence;

ALTER TABLE test_metric DROP CONSTRAINT fke696c5fd3f0c1115;
ALTER TABLE ONLY test_metric ADD CONSTRAINT fke696c5fd3f0c1115 FOREIGN KEY (test_id) REFERENCES test(id) ON DELETE CASCADE;

ALTER TABLE test_metric DROP CONSTRAINT fke696c5fdc4825995;
ALTER TABLE ONLY test_metric ADD CONSTRAINT fke696c5fdc4825995 FOREIGN KEY (metric_id) REFERENCES metric(id) ON DELETE CASCADE;

ALTER TABLE test_execution_tag DROP CONSTRAINT fkf95a5d06e904537f;
ALTER TABLE ONLY test_execution_tag ADD CONSTRAINT fkf95a5d06e904537f FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE;

ALTER TABLE test_execution_tag DROP CONSTRAINT fkf95a5d06fdfcba9a;
ALTER TABLE ONLY test_execution_tag ADD CONSTRAINT fkf95a5d06fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id) ON DELETE CASCADE;

ALTER TABLE public.test_subscriber DROP CONSTRAINT test_subscriber_test_fkey;
ALTER TABLE ONLY public.test_subscriber ADD CONSTRAINT test_subscriber_test_fkey FOREIGN KEY (test_id) REFERENCES test(id) ON DELETE CASCADE;

ALTER TABLE public.test_subscriber DROP CONSTRAINT test_subscriber_user_fkey;
ALTER TABLE ONLY public.test_subscriber ADD CONSTRAINT test_subscriber_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;

ALTER TABLE public.user_group DROP CONSTRAINT user_group_group_fkey;
ALTER TABLE ONLY public.user_group ADD CONSTRAINT user_group_group_fkey FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE;

ALTER TABLE public.user_group DROP CONSTRAINT user_group_user_fkey;
ALTER TABLE ONLY public.user_group ADD CONSTRAINT user_group_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;


COMMIT;
