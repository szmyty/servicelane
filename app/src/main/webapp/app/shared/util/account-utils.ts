
// TODO Quick and dirty methods. Should be refactored.
export const getFullName = (account: any) => {
  const firstName = account.firstName ?? '';
  const lastName = account.lastName ?? '';

  if (firstName === '') {
    return lastName;
  } else {
    return firstName + ' ' + lastName;
  }
};

export const getFullAddress = (account: any) => {
  const street = account.address ?? '';
  const city = account.city ?? '';
  const postal = account.postal ?? '';

  return street + ', ' + city + ', ' + postal;
};

