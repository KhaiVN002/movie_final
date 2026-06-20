import { Dayjs } from "dayjs";

interface MovieUpdateRequest {
    title: string;
    posterURL: string;
    backdropURL: string;
    trailerURL: string;
    overview: string;
    releasedDate: string | Dayjs;
    duration: number;
    originalLanguageId: number;
    subtitleLanguageId: number;
    ageRatingId: number;
    actorIds: number[];
    directorIds: number[];
    addCategoryIds?: number[];
    deleteCategoryIds?: number[];
    backdropURLs?: string[];
}

export type { MovieUpdateRequest };