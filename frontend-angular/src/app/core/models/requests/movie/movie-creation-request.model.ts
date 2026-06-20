import { Dayjs } from "dayjs";

interface MovieCreationRequest {
    title: string;
    posterURL?: string;
    backdropURL?: string;
    trailerURL?: string;
    tagline?: string;
    overview?: string;
    releasedDate?: string | Dayjs;
    duration?: number;
    originalLanguageId?: number;
    subtitleLanguageId?: number;
    ageRatingId?: number;
    actorIds?: number[];
    directorIds?: number[];
    categoryIds?: number[];
    cast?: any[];
    crew?: any[];
}

export type { MovieCreationRequest };
