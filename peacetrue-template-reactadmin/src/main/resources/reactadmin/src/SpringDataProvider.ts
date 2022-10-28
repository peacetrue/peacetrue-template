import {
  CreateParams,
  CreateResult,
  DataProvider,
  DeleteManyParams,
  DeleteManyResult,
  DeleteParams,
  DeleteResult,
  GetListParams,
  GetListResult,
  GetManyParams,
  GetManyReferenceParams,
  GetManyReferenceResult,
  GetManyResult,
  GetOneParams,
  GetOneResult,
  UpdateManyParams,
  UpdateManyResult,
  UpdateParams,
  UpdateResult
} from "react-admin";


interface ParamsRequestInit extends RequestInit {
  params?: Record<string, any>
}

interface DataResponse extends Response {
  data?: Record<string, any>
}

/** extend fetch */
interface Fetch {
  (input: RequestInfo | URL, init?: ParamsRequestInit): Promise<DataResponse>
}

const formatPaginationRequestParams = (params: GetListParams | GetManyReferenceParams): Record<string, any> => {
  let pagination: Record<string, any> = {
    ...params.filter,
    page: String(params.pagination.page - 1),
    size: String(params.pagination.perPage),
  };
  let sort = params.sort;
  if (sort) pagination.sort = `${sort.field},${sort.order || 'asc'}`;
  return pagination;
}

export const formatPaginationResponseData = (data: Record<string, any>): GetListResult | GetManyReferenceResult => {
  return {
    data: data.content,
    total: parseInt(data.totalElements, 10)
  };
}


/**
 * Maps react-admin queries to a REST API implemented using Spring Rest
 *
 * @example
 * GET_LIST             => GET http://my.api.url/posts?keyword=&page=0&size=10&sort=id,asc
 * GET_ONE              => GET http://my.api.url/posts/123
 * GET_MANY             => GET http://my.api.url/posts?id=1234&id=5678
 * GET_MANY_REFERENCE   => GET http://my.api.url/comments?postId=&page=0&size=10&sort=id,asc
 * CREATE               => POST http://my.api.url/posts
 * UPDATE               => PUT http://my.api.url/posts
 * UPDATE_MANY          => multiple call UPDATE
 * DELETE               => DELETE http://my.api.url/posts/123
 * DELETE_MANY          => multiple call DELETE
 */
export const buildSpringDataProvider = (fetch: Fetch): DataProvider => {
  const fetchJson: Fetch = (input, init) => {
    return fetch(input, init)
      .then(response => {
        // after body used, where the body data stored?
        if (response.bodyUsed) return response;
        return response.json().then(data => response.data = data).then(() => response);
      });
  };

  return {
    getList(resource, params: GetListParams): Promise<GetListResult> {
      // https://stackoverflow.com/questions/6566456/how-to-serialize-an-object-into-a-list-of-url-query-parameters
      return fetchJson(`${resource}`, {params: formatPaginationRequestParams(params)})
        .then(response => formatPaginationResponseData(response.data as Record<string, any>));
    },
    getOne(resource, params: GetOneParams): Promise<GetOneResult> {
      return fetchJson(`${resource}/${params.id}`)
        .then(response => ({data: response.data}));
    },
    getMany(resource: string, params: GetManyParams): Promise<GetManyResult> {
      return fetchJson(`${resource}`, {params: {id: params.ids, rtn: 'list'}})
        .then(response => ({data: response.data as []}));
    },
    getManyReference(resource: string, params: GetManyReferenceParams): Promise<GetManyReferenceResult> {
      let springParams = formatPaginationRequestParams(params);
      springParams[params.target] = params.id;
      return fetchJson(`${resource}`, {params: springParams})
        .then(response => formatPaginationResponseData(response.data as Record<string, any>));
    },
    update(resource: string, params: UpdateParams): Promise<UpdateResult> {
      let body = {...params.data, id: params.id};
      let init = {method: 'put', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(body)};
      return fetchJson(`${resource}`, init)
        .then(response => ({data: {id: body.id}}));
    },
    updateMany(resource: string, params: UpdateManyParams): Promise<UpdateManyResult> {
      let promises = params.ids.map(id => this.update(resource, {id: id, data: params.data} as UpdateParams));
      return Promise.all(promises)
        .then(responses => ({data: responses.map(response => response.data.id)}));
    },
    create(resource: string, params: CreateParams): Promise<CreateResult> {
      let init = {method: 'post', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(params.data)};
      return fetchJson(`${resource}`, init)
        .then(response => ({data: response.data}));
    },
    delete(resource: string, params: DeleteParams): Promise<DeleteResult> {
      return fetchJson(`${resource}/${params.id}`, {method: 'delete'})
        .then(response => ({data: {id: params.id}}));
    },
    deleteMany(resource: string, params: DeleteManyParams): Promise<DeleteManyResult> {
      let promises = params.ids.map(id => this.delete(resource, {id: id}));
      return Promise.all(promises).then(responses => ({data: params.ids}));
    }
  };
};

export default buildSpringDataProvider;
