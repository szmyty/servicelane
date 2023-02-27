import { yupResolver } from '@hookform/resolvers/yup';
import { useForm, UseFormProps, UseFormReturn } from 'react-hook-form';
import * as Yup from 'yup';

export function useFormWithSchema<T extends Yup.AnyObjectSchema>(
  schema: T,
  useFormProps?: UseFormProps<Yup.Asserts<T>>,
): UseFormReturn<Yup.Asserts<T>> {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  return useForm({ ...useFormProps, resolver: yupResolver(schema) });
}

export function useFormWithSchemaBuilder<T extends Yup.AnyObjectSchema>(
  schemaBuilder: (yup: typeof Yup) => T,
  useFormProps?: UseFormProps<Yup.Asserts<T>>,
): UseFormReturn<Yup.Asserts<T>> {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  return useForm( { ...useFormProps, resolver: yupResolver(schemaBuilder(Yup)) } );
}
