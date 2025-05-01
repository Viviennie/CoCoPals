// src/types/sharedb-client.d.ts
declare module 'sharedb/lib/client' {
  import * as sharedb from 'sharedb';

  export class Connection {
    constructor(socket: any);
    get(collectionName: string, documentID: string): sharedb.Doc;
  }
}
