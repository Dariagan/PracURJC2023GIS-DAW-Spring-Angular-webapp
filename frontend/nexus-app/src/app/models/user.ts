import { Tweet } from "./tweet.model";

export interface User {
    username: string;
    name: string;
    email: string;
    description: string;
    hasImage:boolean;
    signUpDate: Date;
    role: string;
    tweets: Tweet[];
    blocked: string[];
    following: string[];
    followers: string[];
}
