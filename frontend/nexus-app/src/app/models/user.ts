export interface User {
    username: string;
    name: string;
    email: string;
    description: string;
    hasImage:boolean;
    signUpDate: Date;
    role: string;
    tweets: [];
}
