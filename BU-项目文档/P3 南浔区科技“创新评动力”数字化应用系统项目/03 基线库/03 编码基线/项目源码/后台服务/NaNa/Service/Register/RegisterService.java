package NaNa.Service.Register;

import mHealth.Generic.Service.*;
import NaNa.Dal.Register.*;

public class RegisterService extends BaseService
{
		///#region   初始化信息
	private RegisterDal _dal;

	@Override
	public void init()
	{
		_dal = new RegisterDal();
	}
		///#endregion
}