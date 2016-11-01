-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 1.6 to 1.7                                           --
--                                                                                           --
-----------------------------------------------------------------------------------------------

BEGIN;

ALTER TABLE value_parameter DROP CONSTRAINT fkfd9de65b92d882df;
ALTER TABLE ONLY value_parameter ADD CONSTRAINT fkfd9de65b92d882df FOREIGN KEY (value_id) REFERENCES "value"(id) ON DELETE CASCADE;

ALTER TABLE user_property DROP CONSTRAINT user_property_user_fkey;
ALTER TABLE ONLY user_property ADD CONSTRAINT user_property_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;


COMMIT;
