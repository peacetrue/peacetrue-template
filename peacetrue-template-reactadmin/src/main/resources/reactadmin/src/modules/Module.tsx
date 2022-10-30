// 定义模块相关类型
//安装的模块
export interface InstalledModule {
  readonly default: Module[]
}

//本地的模块
export interface LocalModule {
  readonly default: Module
}

export interface Module {
  readonly resource: JSX.Element
  readonly messages: Record<string, any>
}
