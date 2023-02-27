// @ts-nocheck
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import {useAppDispatch, useAppSelector} from 'app/config/store';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import HomeIcon from '@mui/icons-material/Home';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Calendar, dayjsLocalizer, Views } from 'react-big-calendar';
import dayjs from 'dayjs';
import { FormContainer, FormProvider, useFormContext, CheckboxButtonGroup, useWatch } from 'react-hook-form-mui';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import { Paper } from '@mui/material';
import StepConnector from '@mui/material/StepConnector';
import { RoutePath } from 'app/components/Routes/RoutePath';
import { Link } from 'react-router-dom';
import { Image } from 'mui-image';
import moment from 'moment';
import {getUsers} from "app/shared/reducers/user-management";
import {createEntity, getEntities} from 'app/entities/appointment/appointment.reducer';
import { useTheme } from '@mui/material';

const yesterday = moment().subtract(2, 'day').endOf('day').toDate();

const serviceOptions = [
  { id: '1', label: 'Oil/Oil filter changed' },
  { id: '2', label: 'Scheduled maintenance' },
  { id: '3', label: 'New tires' },
  { id: '4', label: 'Battery replacement' },
  { id: '5', label: 'Brake work' },
  { id: '6', label: 'Antifreeze added' },
  { id: '7', label: 'Engine tune-up' },
  { id: '8', label: 'Wheels aligned/balanced' },
  { id: '9', label: 'Wiper blades replacement' },
  { id: '10', label: 'Replace air filter' },
  { id: '11', label: 'Car Wash' },
];

const schema = yup
  .object({
    services: yup.array().required(),
    startTime: yup.date().required(),
    endTime: yup.date().required(),
  })
  .required();
type FormData = yup.InferType<typeof schema>;

const steps = ['Select Services', 'Schedule Appointment', 'Confirm Appointment'];

const localizer = dayjsLocalizer(dayjs);

function Test() {
  const value = useWatch();
  console.log(value);

  return <></>;
}

function getNextBusinessDay() {
  let dayIncrement = 1;

  if (moment().day() === 5) {
    // set to monday
    dayIncrement = 3;
  } else if (moment().day() === 6) {
    // set to monday
    dayIncrement = 2;
  }

  return moment().add(dayIncrement, 'd').toDate()
}

function isBanned(date: Date) {
  return false;
}

function TimeSlotWrapper(props: { children: React.ReactNode, resource: null /* grid */ | undefined /* gutter */, value: Date }) {
  if (props.resource === undefined /* gutter */ || !isBanned(props.value)) {
    return props.children;
  }

  const child = React.Children.only(props.children);
  // @ts-ignore
  return React.cloneElement(child, { className: child.props.className + ' rbc-off-range-bg' });
}

function isAvailable(target, taken) {
  for (let i = 0; i < taken.length; i++) {
    if (moment(target).isBetween(moment(taken[i].startTime), moment(taken[i].endTime), null, '[)')) {
      return false;
    }
  }

  return true;
}

function HorizontalLinearStepper() {
  const [activeStep, setActiveStep] = useState(0);
  const theme = useTheme();
  const methods = useFormContext();
  const dispatch = useAppDispatch();
  const [events, setEvents] = useState([]);
  const account = useAppSelector(state => state.authentication.account);
  const users = useAppSelector(state => state.userManagement.users);
  const appointmentList = useAppSelector(state => state.appointment.entities);
  const loading = useAppSelector(state => state.appointment.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  useEffect(() => {
    dispatch(getUsers({}));
  }, []);

  const handleSelectSlot = useCallback(
    ({ start, end }) => {
      //setEvents(prev => [...prev, { start, end }]);
      if (isAvailable(start, appointmentList)) {
        setEvents([{start, end}])
      } else {
        console.log("Appointment not available!")
      }
    },
    [appointmentList]
  );

  const handleSelectEvent = useCallback(
    (event) => {
      console.log(event)
    },
    []
  );

  useEffect(() => {
    console.log(appointmentList)
  }, [appointmentList])

  useEffect(() => {
    console.log(events)

    if (events.length > 0) {
      methods.setValue("startTime", events[0].start)
      methods.setValue("endTime", events[0].end)
    } else {
      methods.setValue("startTime", null)
      methods.setValue("endTime", null)
    }

  }, [events]);

  const { defaultDate, scrollToTime } = useMemo(
    () => ({
      defaultDate: getNextBusinessDay(),
      scrollToTime: new Date(1970, 1, 1, 6),
    }),
    []
  );

  const handleFormSubmission = (data: FormData) => {
    console.log(data)
    const entity = {
      created: moment().unix().toString(),
      startTime: moment(data.startTime).unix().toString(),
      endTime: moment(data.endTime).unix().toString(),
      services: data.services.map(service => {
        return service.label
      }).join(','),
      client: users.find(it => it.id.toString() === account.id.toString()),
    };

    console.log("form submitted.")
    console.log(entity)

    dispatch(createEntity(entity));

    handleNext()
  }
  const handleNext = () => {
    setActiveStep(prevActiveStep => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep(prevActiveStep => prevActiveStep - 1);
  };

  const handleReset = () => {
    setActiveStep(0);
    methods.setValue("services", []);
    methods.setValue("startTime", null);
    methods.setValue("endTime", null);
    setEvents([])
  };

  return (
    <Box
      id="--servicelane-form-box"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        width: '100%',
        flexGrow: 1,
        justifyContent: 'center',
        px: 6,
        py: 1,
      }}
    >
      <FormContainer
        formContext={methods}
        onSuccess={handleFormSubmission}
        FormProps={{ id: '--servicelane-form-container', style: { width: '100%', height: '100%', padding: '0px 40px' } }}
      >
        <Test />
        <Stepper
          id="--servicelane-form-stepper"
          connector={<StepConnector id="--servicelane-form-stepper-stepconnector" />}
          activeStep={activeStep}
          sx={{ my: 1 }}
        >
          {steps.map((label, index) => {
            const stepProps: { completed?: boolean } = {};
            const labelProps: {
              optional?: React.ReactNode;
            } = {};
            return (
              <Step key={label} {...stepProps}>
                <StepLabel {...labelProps}>{label}</StepLabel>
              </Step>
            );
          })}
        </Stepper>
        <Paper
          id="--servicelane-form-stepper-content"
          elevation={0}
          sx={{
            p: 0,
            m: 0,
            justifyContent: 'center',
            display: 'flex',
            flexDirection: 'column',
            width: '100%',
            flexGrow: 1,
            height: '90%',
            backgroundColor: '#303030',
          }}
        >
          {activeStep === steps.length ? (
            <Box
              id="--servicelane-select-services-container"
              sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                flexDirection: 'column',
                minHeight: '100%',
              }}
            >
              <Paper
                sx={{
                  display: 'flex',
                  justifyContent: 'top',
                  alignItems: 'center',
                  flexDirection: 'column',
                  p: 1,
                  minHeight: '100%',
                  minWidth: '50%',
                }}
              >
                <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={2}>
                  <Image src="content/images/car.png" duration={500} height={400} width={450} />
                  <Typography color="#303030" align="center" variant="h6">
                    Reservation Accepted
                  </Typography>
                  <Box sx={{ display: 'flex', flexDirection: 'row' }}>
                    <Button color="inherit" variant="outlined" component={Link} to={RoutePath.Home} sx={{ mr: 1 }} startIcon={<HomeIcon />}>
                      Go Home
                    </Button>
                    <Box sx={{ flex: '1 1 auto' }} />
                    <Button variant="outlined" onClick={handleReset}>
                      Start Over
                    </Button>
                  </Box>
                </Stack>
              </Paper>
            </Box>
          ) : (
            <>
              {activeStep == 0 && (
                <Box
                  id="--servicelane-select-services-container"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    flexDirection: 'column',
                    minHeight: '100%',
                  }}
                >
                  <Paper
                    sx={{
                      display: 'flex',
                      justifyContent: 'top',
                      alignItems: 'center',
                      flexDirection: 'column',
                      p: 1,
                      minHeight: '100%',
                      minWidth: '50%',
                    }}
                  >
                    <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={1}>
                      <Typography color="#303030" align="center" variant="h5" sx={{ mt: 1 }}>
                        What needs fixing?
                      </Typography>
                      <CheckboxButtonGroup name={'services'} returnObject={true} required options={serviceOptions} />

                      <Button variant={'outlined'} onClick={handleNext}>
                        Next
                      </Button>
                    </Stack>
                  </Paper>
                </Box>
              )}

              {activeStep == 1 && (
                <Box
                  id="--servicelane-schedule-appointment-container"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    flexDirection: 'column',
                    minHeight: '100%',
                  }}
                >
                  <Paper
                    sx={{
                      display: 'flex',
                      justifyContent: 'top',
                      alignItems: 'center',
                      flexDirection: 'column',
                      py: 2,
                      px: 4,
                      minHeight: '100%',
                      minWidth: '80%',
                    }}
                  >
                    <Stack sx={{width: '100%'}} direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={1}>
                      <Calendar
                        dayLayoutAlgorithm={'no-overlap'}
                        defaultDate={defaultDate}
                        defaultView={Views.WORK_WEEK}
                        localizer={localizer}
                        events={events}
                        views={['work_week']}
                        onSelectEvent={handleSelectEvent}
                        onSelectSlot={handleSelectSlot}
                        selectable
                        startAccessor="start"
                        endAccessor="end"
                        min={moment({hour: 8}).toDate()}
                        max={moment({hour: 21}).toDate()}
                        step={60}
                        style={{ height: 500 }}
                        components={{ timeSlotWrapper: (timeSlotWrapperProps) => {
                            //console.log(timeSlotWrapperProps.value)

                            const available = isAvailable(timeSlotWrapperProps.value, appointmentList)

                            const style = {
                              display: 'flex',
                              flex: 1,
                              borderLeft: '1px solid #DDD',
                              // backgroundColor: available ? '#fff' : '#f5f5dc',
                              backgroundColor: available ? '#fff' : theme.palette.error.main,
                            }

                            return (
                              <div style={style}>
                                {timeSlotWrapperProps.children}
                              </div>
                            )
                          }, }}
                      />
                      <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
                        <Button color="inherit" variant={'outlined'} onClick={handleBack} sx={{ mr: 1 }}>
                          Back
                        </Button>
                        <Box sx={{ flex: '1 1 auto' }} />
                        <Button variant={'outlined'} onClick={handleNext}>
                          Next
                        </Button>
                      </Box>
                    </Stack>
                  </Paper>
                </Box>
              )}

              {activeStep === steps.length - 1 && (
                <Box
                  id="--servicelane-select-services-container"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    flexDirection: 'column',
                    minHeight: '100%',
                  }}
                >
                  <Paper
                    sx={{
                      display: 'flex',
                      justifyContent: 'top',
                      alignItems: 'center',
                      flexDirection: 'column',
                      p: 1,
                      minHeight: '100%',
                      minWidth: '60%',
                    }}
                  >
                    <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={2}>
                      <Typography color="#303030" align="center" variant="h5" sx={{ mt: 1 }}>
                        Did we get everything?
                      </Typography>
                      <Stack direction="row" spacing={10}>
                        <CheckboxButtonGroup
                          name="services"
                          returnObject={true}
                          disabled
                          options={serviceOptions}
                          labelProps={{ sx: { height: '32px' } }}
                        />
                        <Box>
                          <Typography color="#303030" align="center" variant="subtitle1" sx={{ mt: 1 }}>
                            Date Selected: {methods.getValues().startTime.toLocaleDateString('en-us', { weekday:"long", year:"numeric", month:"short", day:"numeric"})}
                          </Typography>
                          <Typography color="#303030" align="center" variant="subtitle1" sx={{ mt: 1 }}>
                            Time Selected: {methods.getValues().startTime.toLocaleTimeString()}
                          </Typography>
                        </Box>
                      </Stack>

                      <Box sx={{ display: 'flex', flexDirection: 'row' }}>
                        <Button color="inherit" variant={'outlined'} onClick={handleBack} sx={{ mr: 1 }}>
                          Back
                        </Button>
                        <Box sx={{ flex: '1 1 auto' }} />
                        <Button variant='outlined' type="submit">
                          Submit Reservation
                        </Button>
                      </Box>

                      <Button onClick={handleReset}>Start Over</Button>
                    </Stack>
                  </Paper>
                </Box>
              )}
            </>
          )}
        </Paper>
      </FormContainer>
    </Box>
  );
}

export default function Reservation() {
  const account = useAppSelector(state => state.authentication.account);
  const methods = useForm<FormData>({
    defaultValues: {
      services: [],
      startTime: null,
      // startTime: moment().toDate(),
      // endTime: moment().add(1, 'hour').toDate(),
      endTime: null,
    },
    resolver: yupResolver(schema),
  });
  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      //location.href = `${location.origin}${redirectURL}`;
    }
  });

  return (
    <FormProvider {...methods} >
      <HorizontalLinearStepper />
    </FormProvider>
  )
}
