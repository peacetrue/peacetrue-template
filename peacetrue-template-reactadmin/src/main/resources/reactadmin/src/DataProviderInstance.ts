import qs from "qs";
import jsonServerProvider from 'ra-data-json-server';
import buildSpringDataProvider from "./SpringDataProvider";

function stringify(params: Record<string, any>) {
  return qs.stringify(params, {
    arrayFormat: 'repeat',
    serializeDate: (date: Date) => date.getTime().toString(),
    allowDots: true,
  });
}

// 参考 https://marmelab.com/react-admin/DataProviderWriting.html，使用 Spring 接口风格
export const dataProvider = process.env.REACT_APP_BASE_URL === 'https://jsonplaceholder.typicode.com'
  ? jsonServerProvider(process.env.REACT_APP_BASE_URL)
  : buildSpringDataProvider((input, init) => {
    let params = init?.params ? `?${stringify(init.params)}` : "";
    return fetch(`${process.env.REACT_APP_BASE_URL}/${input}${params}`, init);
  });
