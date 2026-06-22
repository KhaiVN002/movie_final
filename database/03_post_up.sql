
DO $$
DECLARE
    rec RECORD;
    next_val BIGINT;
BEGIN
    FOR rec IN
        SELECT table_name, column_name
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND is_identity = 'YES'
          AND column_name = 'id'
          AND table_name <> 'refresh_tokens'
        ORDER BY table_name
    LOOP

        EXECUTE format('SELECT COALESCE(MAX(%I), 0) + 1 FROM %I', rec.column_name, rec.table_name) INTO next_val;
        

        EXECUTE format('ALTER TABLE %I ALTER COLUMN %I RESTART WITH %s', rec.table_name, rec.column_name, next_val);
        
        RAISE NOTICE 'Đã đồng bộ: %.% sẽ bắt đầu từ %', rec.table_name, rec.column_name, next_val;
    END LOOP;
END;
$$;
