import NoSsr from '@mui/material/NoSsr';
import React, { createContext, PropsWithChildren, useContext } from 'react';
import GoogleFontLoader, { GoogleFontLoaderProps } from 'react-google-font-loader';

export const FontContext = createContext<GoogleFontLoaderProps['fonts']>([]);

export function useFonts() {
  return useContext(FontContext);
}

export function FontProvider({ fonts, children }: PropsWithChildren<GoogleFontLoaderProps>) {
  return (
    <>
      <NoSsr>
        <GoogleFontLoader fonts={fonts} />
      </NoSsr>
      <FontContext.Provider value={fonts}>{children}</FontContext.Provider>
    </>
  );
}
