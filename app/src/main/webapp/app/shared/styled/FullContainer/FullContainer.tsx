import * as React from 'react';
import { PropsWithChildren } from 'react';
import { FullContainerProps } from './FullContainer.types';
import { Container } from '@mui/material';

export default function FullContainer({ children, ...props }: PropsWithChildren<FullContainerProps>) {
  return (
    <Container maxWidth={false} disableGutters={true} sx={{ m: 0, p: 0 }} {...props}>
      {children}
    </Container>
  );
}
