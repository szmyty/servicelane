import React, { useState } from 'react';
import { AppBar as MuiAppBar } from '@mui/material';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import Avatar from '@mui/material/Avatar';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import { RoutePath } from 'app/components/Routes/RoutePath';
import NavItem from 'app/components/NavItem';
import { AppBarProps } from 'app/components/AppBar/AppBar.types';
import _ from 'lodash';
import { NavLink, useLocation } from 'react-router-dom';

const pages = ['Home', 'Make Reservation', 'My Account'];
const settings = ['Profile', 'Account', 'Dashboard', 'Logout'];

const AppBar = React.forwardRef<HTMLDivElement, AppBarProps>(({ account, isAuthenticated }, ref) => {
  const [anchorElUser, setAnchorElUser] = useState<null | HTMLElement>(null);
  const location = useLocation();

  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  console.log(account)

  return (
    <MuiAppBar ref={ref} position="static">
      <Toolbar disableGutters sx={{ mx: 2 }}>
        <Grid container spacing={1} direction="row" justifyContent="space-between" alignItems="center">
          <Grid item />
          <Grid item>
            <Grid container spacing={1} direction="row" alignItems="center">
              <NavItem text={pages[0]} route={RoutePath.Home} />
              <NavItem text={pages[1]} route={RoutePath.Reservation} />
              <NavItem text={pages[2]} route={RoutePath.Account} />
            </Grid>
          </Grid>

          <Grid item>
            <Box>
              <Tooltip title="Open settings">
                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                  <Avatar alt={_.capitalize(account?.firstName ?? account?.login ?? 'Anonymous')} src="/static/images/avatar/2.jpg" />
                </IconButton>
              </Tooltip>
              <Menu
                sx={{ mt: '45px' }}
                id="menu-appbar"
                anchorEl={anchorElUser}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorElUser)}
                onClose={handleCloseUserMenu}
              >
                {isAuthenticated ?
                  [
                    <MenuItem key='Logout' component={NavLink} to={RoutePath.Logout} onClick={handleCloseUserMenu}>
                      <Typography textAlign="center">Logout</Typography>
                    </MenuItem>
                  ]
                  :
                  [
                    <MenuItem key='Sign In' component={NavLink} to={{
                      pathname: RoutePath.Login,
                      search: location.search,
                    }}
                              replace
                              state={{ from: location }} onClick={handleCloseUserMenu}>
                      <Typography textAlign="center">Sign In</Typography>
                    </MenuItem>
                  ]
                }
              </Menu>
            </Box>
          </Grid>
        </Grid>
      </Toolbar>
    </MuiAppBar>
  );
});

export default AppBar;
