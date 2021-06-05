import Location from "../models/location";
import { ILocation } from "../types/types";
import mongoose from "mongoose";

export async function createLocation(location: ILocation): Promise<ILocation> {
  // create new location
  return new Promise<ILocation>((resolve, reject) => {
    const locationModel = new Location({
      latitude: location.latitude,
      longitude: location.longitude,
      country: location.country ?? null,
      city: location.city ?? null,
      postalCode: location.postalCode ?? null,
      address: location.address ?? null,
    });
    locationModel
      .save()
      .then((doc: ILocation) => resolve(doc))
      .catch((err) => reject(err));
  });
}

export async function updateLocation(
  existingLocationId: string,
  location: ILocation
): Promise<ILocation | null> {
  return Location.findOneAndUpdate(
    { _id: existingLocationId },
    {
      latitude: location.latitude,
      longitude: location.longitude,
      city: location?.city,
      postalCode: location?.postalCode,
      address: location?.address,
    }
  );
}

export async function getLocation(parent, args, context, info): Promise<ILocation> {
  return (await Location.findById(args.id)) as ILocation;
}

export async function getLocations(parent, args, context, info): Promise<ILocation[]> {
  // find by given optional ids
  if (args.ids !== undefined) {
    const ids = args.ids.map((id: string) => mongoose.Types.ObjectId(id));
    return (await Location.find({ _id: { $in: ids } })) as ILocation[];
  } else {
    return (await Location.find()) as ILocation[];
  }
}
