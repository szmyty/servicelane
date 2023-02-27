import { useLocation } from 'react-router-dom';
import  { useEffect, useState } from 'react';

export type LocationState = {
  from?: {
    path?: string
  },
  background?: Partial<Location>,
};

export const useLocationState = () => {
  const location = useLocation();
  const [state, setState] = useState<LocationState | null>(null);
  const [background, setBackground] = useState<Partial<Location> | undefined>(undefined);
  const [isBackground, setIsBackground] = useState<boolean>(false);

  useEffect(() => {
    setState(location.state as LocationState);
  }, [location]);

  useEffect(() => {
    setBackground(state.background ?? undefined);
  }, [state]);

  useEffect(() => {
    setIsBackground(Boolean(background));
  }, [background]);

  return { location, state, background, isBackground } as const;
};
