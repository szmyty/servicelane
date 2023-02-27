import React from 'react';
import { createContext, PropsWithChildren, useContext, useMemo, useState } from 'react';
import { createTheme, PaletteMode, responsiveFontSizes } from '@mui/material';
import { ThemeProvider as MuiThemeProvider } from '@mui/material/styles';
import { useFonts } from './FontContext';
import _ from 'lodash';
import { getTheme, ThemeMode } from 'app/shared/theme';

export type ThemeContextType = {
  toggleTheme: () => void;
};

const ThemeContext = createContext<ThemeContextType>({
  toggleTheme: () => console.warn('No theme provider.'),
});

export function useToggleTheme() {
  return useContext(ThemeContext);
}

export function ThemeProvider({ children }: PropsWithChildren<Record<never, never>>) {
  const [mode, setMode] = useState<PaletteMode>(ThemeMode.Light);
  const fonts = useFonts();

  const fontNames = useMemo(() => _.map(fonts, 'font'), [fonts]);

  function toggleTheme() {
    setMode((prevMode: PaletteMode) => (prevMode === ThemeMode.Light ? ThemeMode.Dark : ThemeMode.Light));
  }

  const theme = useMemo(() => responsiveFontSizes(createTheme(getTheme(mode, fontNames))), [mode, fontNames]);

  return (
    <ThemeContext.Provider value={{ toggleTheme }}>
      <MuiThemeProvider theme={theme}>{children}</MuiThemeProvider>
    </ThemeContext.Provider>
  );
}
