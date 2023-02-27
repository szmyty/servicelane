import { CSSProperties } from 'react';
import { PaletteMode } from '@mui/material';
import { deepOrange } from '@mui/material/colors';

export enum ThemeMode {
  Dark = 'dark',
  Light = 'light',
}

export const getTheme = (mode: PaletteMode, fonts: string[]) => ({
  // Custom color fields.
  status: {
    danger: deepOrange[500],
  },
  palette: {
    mode,
    contrastThreshold: 3,
    ...(mode === ThemeMode.Light
      ? {
          primary: {
            main: '#3f51b5',
          },
          secondary: {
            main: '#f50057',
          },
          neutral: {
            main: '#6a6e73',
            contrastText: '#fff',
          },
        }
      : {
          primary: {
            main: '#fce4ec',
            dark: '#fce4ec',
          },
          secondary: {
            main: '#b3e5fc',
          },
          neutral: {
            main: '#647488',
            contrastText: '#fff',
          },
        }),
  },
  typography: {
    fontFamily: [
      fonts,
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
      '"Apple Color Emoji"',
      '"Segoe UI Emoji"',
      '"Segoe UI Symbol"',
    ].join(','),
  },
  components: {
    MuiStepConnector: {
      styleOverrides: {
        root: ({ theme }) => ({
          '& .MuiStepConnector-line': {
            borderColor: theme.palette.common.white,
          },
          '&.Mui-active': {
            '& .MuiStepConnector-line': {
              borderColor: theme.palette.secondary.main,
            },
          },
          '&.Mui-completed': {
            '& .MuiStepConnector-line': {
              borderColor: theme.palette.primary.main,
            },
          },
        }),
      },
    },
    MuiStepLabel: {
      styleOverrides: {
        root: ({ theme }) => ({
          '& .MuiStepLabel-label': {
            color: theme.palette.grey[500],
          },
        }),
        label: ({ theme }) => ({
          '&.Mui-active': {
            color: theme.palette.primary.main,
          },
          '&.Mui-completed': {
            color: theme.palette.primary.main,
          },
        }),
      },
    },
  },
});

declare module '@mui/material/styles' {
  interface Theme {
    status: {
      danger: CSSProperties['color'];
    };
  }

  interface ThemeOptions {
    status: {
      danger: CSSProperties['color'];
    };
  }

  interface Palette {
    neutral: Palette['primary'];
  }

  interface PaletteOptions {
    neutral: PaletteOptions['primary'];
  }

  interface PaletteColor {
    darker?: string;
  }

  interface SimplePaletteColorOptions {
    darker?: string;
  }
}
