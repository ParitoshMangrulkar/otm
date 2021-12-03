Menu Level
----------

select a.user_access_gid, c.gl_level, c.user_role_gid, b.user_menu_layout_gid
from USER_ACCESS a, USER_MENU_ACCESS b , USER_ROLE c
where a.user_access_gid = b.user_access_gid
and a.USER_ROLE_GID = c.USER_ROLE_GID
and a.domain_name  = 'APPL'

How SQLs are run with Domain
-----------------------------
Doc ID 2825814.1

Default VPD predicate:
select a.shipment_gid,b.status_type_xid
from shipment_status a, status_type b
where a.status_type_gid = b.status_type_gid
and a.insert_date > sysdate - 30;

Actually SQL is converted to following for domain:

select a.shipment_gid,b.status_type_xid
from (select * from shipment_status
where EXISTS (SELECT 1 FROM domain_grants_made_v WHERE grantee_domain LIKE 'GUEST' AND domain_name LIKE grantor_domain AND table_name = 'SHIPMENT_STATUS')
)
a,
(select * from status_type
where EXISTS (SELECT 1 FROM domain_grants_made_v WHERE grantee_domain LIKE 'GUEST' AND domain_name LIKE grantor_domain AND table_name = 'STATUS_TYPE')

)b
where a.status_type_gid = b.status_type_gid
and a.insert_date > sysdate - 30;
----------------------------------------

