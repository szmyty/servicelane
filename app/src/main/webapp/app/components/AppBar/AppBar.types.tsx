import { AppBarProps as MuiAppBarProps } from '@mui/material';

export type AppBarProps = MuiAppBarProps & {
  account: any;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
  currentLocale: string;
};
