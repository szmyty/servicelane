CREATE TABLE public.customer
(
    uuid uuid DEFAULT gen_random_uuid (),
    first_name text NOT NULL,
    last_name text NOT NULL,
    vehicle_make text NOT NULL,
    vehicle_model text NOT NULL,
    vehicle_color text NOT NULL,
    vin_number text NOT NULL,
    credit_card text NOT NULL,
    street_address text NOT NULL,
    city text NOT NULL,
    state text NOT NULL,
    zip text NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE IF EXISTS public.customer
    OWNER to app;

COMMENT ON COLUMN public.customer.uuid
    IS 'Universally Unique Identifier (UUID) for the customer.';

COMMENT ON COLUMN public.customer.first_name
    IS 'Customer''s First Name';

COMMENT ON COLUMN public.customer.last_name
    IS 'Customer''s Last Name';

COMMENT ON COLUMN public.customer.vehicle_make
    IS 'Vehicle Make';

COMMENT ON COLUMN public.customer.vehicle_model
    IS 'Vehicle Model';

COMMENT ON COLUMN public.customer.vehicle_color
    IS 'Vehicle Color';

COMMENT ON COLUMN public.customer.vin_number
    IS 'Vehicle VIN';

COMMENT ON COLUMN public.customer.credit_card
    IS 'Credit Card';

COMMENT ON COLUMN public.customer.street_address
    IS 'Home Street';

COMMENT ON COLUMN public.customer.city
    IS 'Home City';

COMMENT ON COLUMN public.customer.state
    IS 'Home State';

COMMENT ON COLUMN public.customer.zip
    IS 'Home Zip Code';
