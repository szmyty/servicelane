# Running application as a service.

Reference: https://stackoverflow.com/a/28704296
 
$ systemctl start whatever_you_want.service # starts the service
$ systemctl enable whatever_you_want.service # auto starts the service
$ systemctl disable whatever_you_want.service # stops autostart
$ systemctl stop whatever_you_want.service # stops the service
$ systemctl restart whatever_you_want.service # restarts the service
