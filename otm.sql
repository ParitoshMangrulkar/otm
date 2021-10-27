Menu Level
----------

select a.user_access_gid, c.gl_level, c.user_role_gid, b.user_menu_layout_gid
from USER_ACCESS a, USER_MENU_ACCESS b , USER_ROLE c
where a.user_access_gid = b.user_access_gid
and a.USER_ROLE_GID = c.USER_ROLE_GID
and a.domain_name  = 'APPL'
