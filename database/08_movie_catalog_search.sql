BEGIN;

CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE OR REPLACE FUNCTION unaccent_immutable(TEXT)
RETURNS TEXT
LANGUAGE SQL
IMMUTABLE
PARALLEL SAFE
AS $$
    SELECT public.unaccent('public.unaccent', $1)
$$;

CREATE INDEX IF NOT EXISTS idx_movies_title_unaccent_trgm
    ON movies USING GIN ((unaccent_immutable(LOWER(title))) gin_trgm_ops);

COMMIT;
