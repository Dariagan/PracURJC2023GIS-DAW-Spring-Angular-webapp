import { User } from "./user";

export interface Tweet{
    id: number;
    author:User;
    text:string;
    date:Date;
    hasMedia:boolean;
    likes:string[];
    shares:number[];
    reporters:string[];
    children:number[];
}