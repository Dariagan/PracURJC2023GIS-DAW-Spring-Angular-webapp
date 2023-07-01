import { User } from "./user";


export interface Tweet{

    id: number;
    author:User;
    text:string;
    date:Date;
    likes:User[];
    shares:User[];
    reporters:User[];
    urlToProfilePic:string;
}