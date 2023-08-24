INSERT INTO "users" (id,notify_mail,confirmuuid,username,password) values (10000,'user@example-host.abc',null,'test','$2a$10$Ms36nFnVSBYYcXkH0Ib6.uDRNjohZymdXThpiNJaz7CIm/mVDvxqK');

INSERT INTO "domain_watcher" (id,regex,search_term,send_mail,mail_on_update,user_id,active) values (10000,false,'test',false,false,10000,true);

